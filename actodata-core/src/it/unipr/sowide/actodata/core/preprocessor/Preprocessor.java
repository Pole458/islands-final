package it.unipr.sowide.actodata.core.preprocessor;

import it.unipr.sowide.actodata.core.actodesext.ActoDataBaseBehavior;
import it.unipr.sowide.actodata.core.actodesext.ActoDataCaseFactory;
import it.unipr.sowide.actodata.core.dataflow.PushReceiver;
import it.unipr.sowide.actodata.core.dataflow.PushSender;
import it.unipr.sowide.actodata.core.dataflow.Data;
import it.unipr.sowide.actodes.actor.Behavior;
import it.unipr.sowide.actodes.registry.Reference;
import it.unipr.sowide.util.annotations.Open;

import java.util.function.Consumer;

/**
 * A partial specialization of a <i>preprocessor</i>. A preprocessor reacts to
 * incoming {@link Data} messages with the purpose of eventually emitting
 * {@link Data} messages to other actors.
 *
 * @param <I> the type of the input data.
 * @param <O> the type of the output data.
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public abstract class Preprocessor<I, O> extends ActoDataBaseBehavior {

    private final PushSender sender = new PushSender();
    @SuppressWarnings({"unchecked", "RedundantCast", "rawtypes"})
    private final PushReceiver<Data<I>> receiver = new PushReceiver<>(
            (Class<? extends Data<I>>) (Class<? extends Data>) Data.class,
            (iData, message) -> {
                preprocessWrappedData(iData, (o) -> {
                    sender.sendData(this, this.wrapData(o));
                });
            }
    );

    /**
     * {@inheritDoc}
     **/
    @Override
    public final void actoDataNodeCases(ActoDataCaseFactory c) {
        receiver.defineFragmentCases(this, c);
        sender.defineFragmentCases(this, c);
        preprocessorCases(c);
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public final void onStart() {
        super.onStart();
        setup();
    }

    /**
     * Defines what the preprocessors does at startup.
     */
    @Open
    public void setup() {
        // override if needed
    }

    /**
     * Defines custom cases (i.e. message pattern/handler pairs) of this
     * preprocessor
     *
     * @param c the case factory
     */
    public void preprocessorCases(ActoDataCaseFactory c) {
        // override if necessary
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public void onShutdown() {
        super.onShutdown();
        receiver.unsubscribeAll(this);
    }

    /**
     * Preprocesses data wrapped behind a {@link Data} message.
     *
     * @param input          the data message
     * @param resultAcceptor an acceptor that eventually takes output to send to
     *                       the output pipelines
     */
    @Open
    public void preprocessWrappedData(
            Data<I> input,
            Consumer<O> resultAcceptor
    ) {
        input.getData(this).then(i -> preprocess(i, resultAcceptor)).compel();
    }

    /**
     * Preprocesses data.
     *
     * @param input the input data
     * @param resultAcceptor an acceptor that eventually takes output to send to
     *                       the output pipelines
     */
    public abstract void preprocess(I input, Consumer<O> resultAcceptor);

    /**
     * Specialization of {@link ActoDataBaseBehavior#changeBehavior(Behavior)}
     * used for preprocessor instances. If this method is used to change the
     * behavior to another {@link Preprocessor}, everything that represent the
     * internal state of a generic preprocessor is copied into {@code b}. This
     * allows to keep structural continuity of the application (e.g. by copying
     * the list of subscribers).
     *
     * @param b the new preprocessor behavior
     */
    protected void changeBehavior(Preprocessor<I, O> b) {
        for (Reference subscriber : sender.getSubscribers()) {
            b.sender.addSubscriber(subscriber);
        }
        for (Reference registeredSender : receiver.getRegisteredSenders()) {
            b.receiver.addRegisteredSender(registeredSender);
        }
        super.changeBehavior(b);
    }

}
