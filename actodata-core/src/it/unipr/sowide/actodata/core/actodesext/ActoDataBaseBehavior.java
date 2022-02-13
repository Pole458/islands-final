package it.unipr.sowide.actodata.core.actodesext;

import it.unipr.sowide.actodata.core.acquirer.Acquirer;
import it.unipr.sowide.actodata.core.actodesext.content.DoJob;
import it.unipr.sowide.actodata.core.actodesext.content.Query;
import it.unipr.sowide.actodata.core.actodesext.content.QueryProperty;
import it.unipr.sowide.actodata.core.actodesext.promise.ActoPromise;
import it.unipr.sowide.actodata.core.controller.Controller;
import it.unipr.sowide.actodata.core.dataflow.CarriedData;
import it.unipr.sowide.actodata.core.dataflow.Data;
import it.unipr.sowide.actodata.core.dataset.DataSetManager;
import it.unipr.sowide.actodata.core.engine.Engine;
import it.unipr.sowide.actodata.core.master.Master;
import it.unipr.sowide.actodata.core.master.content.GlobalStart;
import it.unipr.sowide.actodata.core.master.MasterInterface;
import it.unipr.sowide.actodata.core.master.content.NodeShuttingDown;
import it.unipr.sowide.actodata.core.preprocessor.Preprocessor;
import it.unipr.sowide.actodata.core.reporter.Reporter;
import it.unipr.sowide.actodes.actor.Behavior;
import it.unipr.sowide.actodes.actor.CaseFactory;
import it.unipr.sowide.actodes.actor.Message;
import it.unipr.sowide.actodes.actor.MessageHandler;
import it.unipr.sowide.actodes.interaction.Done;
import it.unipr.sowide.actodes.interaction.Error;
import it.unipr.sowide.actodes.registry.Reference;
import it.unipr.sowide.util.annotations.Open;
import it.unipr.sowide.util.annotations.PackagePrivate;

import java.util.Optional;
import java.util.function.Supplier;

import static it.unipr.sowide.actodata.core.actodesext.IsInstancePatterns.isInstanceContentPattern;
import static it.unipr.sowide.util.CheckedCast.cast;

/**
 * A partial implementation of a behavior that defines some common operation
 * of the ActoDatA actors. Every {@link Acquirer}, {@link Preprocessor},
 * {@link Controller}, {@link DataSetManager}, {@link Engine}, {@link Reporter}
 * and {@link Master} is an extension of this behavior.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public abstract class ActoDataBaseBehavior
        extends Behavior
        implements ActoDataBehaviorActions, ActoDataBehaviorEvents {

    @PackagePrivate
    Behavior nextBehavior = null;

    private MasterInterface masterInterface = null;


    @PackagePrivate
    void resetChangeBehavior() {
        nextBehavior = null;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public void changeBehavior(Behavior b) {
        nextBehavior = b;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public final void cases(CaseFactory c) {

        c.define(isInstanceContentPattern(GlobalStart.class), (message) -> {
            masterInterface = new MasterInterface(this, message.getSender());
            onGlobalStart(masterInterface);
            return null;
        });

        c.define(START, (__) -> {
            onStart();
            return null;
        });

        c.define(CYCLE, (__) -> {
            onCycle();
            return null;
        });

        actoDataNodeCases(new ActoDataCaseFactory(this, c));

        MessageHandler acceptAllHandler = this.defineAcceptAll();
        if (acceptAllHandler != null) {

            c.define(ACCEPTALL, acceptAllHandler);
        }
    }

    /**
     * Defines the "cases" (i.e. message pattern - message handler pairs) of
     * this behavior using an {@link ActoDataCaseFactory}.
     *
     * @param c the case factory
     */
    public abstract void actoDataNodeCases(ActoDataCaseFactory c);

    /**
     * {@inheritDoc}
     **/
    @Open
    @Override
    public void onStart() {
        // override if needed
    }

    /**
     * {@inheritDoc}
     **/
    @Open
    @Override
    public void onCycle() {
        // override if needed
    }

    /**
     * {@inheritDoc}
     **/
    @Open
    @Override
    public void onShutdown() {
        if (masterInterface != null) {
            masterInterface.informMaster(NodeShuttingDown.INSTANCE);
        }
        //override if needed
    }

    /**
     * {@inheritDoc}
     **/
    @Open
    @Override
    public void onGlobalStart(MasterInterface masterInterface) {
        // override if needed
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public final Optional<MasterInterface> getMasterInterface() {
        return Optional.ofNullable(masterInterface);
    }

    /**
     * Override this to define what to do with unmatched messages.
     *
     * @return the message handler that will be used to handle ACCEPTALL
     * patterns; if null, no case will be created for the ACCEPTALL pattern
     */
    @Open
    public MessageHandler defineAcceptAll() {
        return (m) -> null;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public <R>
    ActoPromise<R> queryProperty(
            Reference r,
            String propertyName,
            Class<? extends R> resultType
    ) {
        return new ActoPromise<>((resolver, rejecter) -> {
            future(r, new Query<>(new QueryProperty(propertyName)), m -> {
                if (m.getContent() instanceof Error) {
                    rejecter.reject((Error) m.getContent());
                } else if (resultType.isInstance(m.getContent())) {
                    @SuppressWarnings("unchecked")
                    R content = (R) m.getContent();
                    resolver.resolve(content);
                } else {
                    rejecter.reject(Error.UNEXPECTEDREPLY);
                }
                return null;
            });
        });
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public <R>
    ActoPromise<R> queryProperty(
            Reference r,
            String propertyName,
            R typedNull
    ) {
        return new ActoPromise<>((resolver, rejecter) -> {
            future(r, new Query<>(new QueryProperty(propertyName)), (m) -> {
                if (m.getContent() instanceof Error) {
                    rejecter.reject((Error) m.getContent());
                } else {
                    cast(typedNull, m.getContent()).ifSuccessfulOrElse(
                            resolver::resolve,
                            (/*else*/) -> {
                                rejecter.reject(Error.UNEXPECTEDREPLY);
                            }
                    );
                }
                return null;
            });
        });
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public <T> ActoPromise<T> tempWorker(Supplier<T> job) {
        Reference workerActor = actor(new Worker());
        return promiseFuture(workerActor, (DoJob<T>) job::get);
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public <T> ActoPromise<T> promiseFuture(
            Reference r,
            Object o,
            Class<? extends T> expectedResponseType
    ) {
        return new ActoPromise<>(((resolver, rejecter) -> {
            future(r, o, (msg) -> {
                if (msg.getContent() instanceof Error) {
                    rejecter.reject((Error) msg.getContent());
                } else if (expectedResponseType.isInstance(msg.getContent())) {
                    @SuppressWarnings("unchecked")
                    T content = (T) msg.getContent();
                    resolver.resolve(content);
                } else {
                    rejecter.reject(Error.UNEXPECTEDREPLY);
                }
                return null;
            });
        }));
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public <T> ActoPromise<T> promiseFuture(
            Reference r,
            Object o,
            T typedNull
    ) {
        return promiseFuture(r, o);
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public <T> ActoPromise<T> promiseFuture(Reference r, Object o) {
        return new ActoPromise<>(((resolver, rejecter) -> {
            future(r, o, (responseMessage) -> {
                if (responseMessage.getContent() instanceof Error) {
                    rejecter.reject((Error) responseMessage.getContent());
                } else {
                    cast((T) null, responseMessage.getContent())
                            .ifSuccessfulOrElse(
                                    resolver::resolve,
                                    (/*else*/) -> {
                                        rejecter.reject(Error.UNEXPECTEDREPLY);
                                    }
                            );
                }

                return null;
            });
        }));
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public void done(Message message) {
        send(message, Done.DONE);
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public <T> Data<T> wrapData(T object) {
        return new CarriedData<>(object);
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public <T> ActoPromise<T> unwrapData(Data<T> data) {
        return data.getData(this);
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public void actorLog(String s) {
        //TODO use improved logger system with custom messages
//        System.out.println(getReference().getName() + ": " + s);
    }

}
