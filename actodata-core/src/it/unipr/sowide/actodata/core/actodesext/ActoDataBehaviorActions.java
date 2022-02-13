package it.unipr.sowide.actodata.core.actodesext;

import it.unipr.sowide.actodata.core.actodesext.promise.ActoPromise;
import it.unipr.sowide.actodata.core.dataflow.CarriedData;
import it.unipr.sowide.actodata.core.dataflow.Data;
import it.unipr.sowide.actodata.core.master.Master;
import it.unipr.sowide.actodata.core.master.MasterInterface;
import it.unipr.sowide.actodata.core.master.content.GlobalStart;
import it.unipr.sowide.actodes.actor.Behavior;
import it.unipr.sowide.actodes.registry.Reference;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Interface containing methods of a {@link ActoDataBaseBehavior} that
 * represents some common useful actions. This interface is mainly meant to
 * expose some functionality of an {@link ActoDataBaseBehavior} to other objects
 * in the implementation of the library.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public interface ActoDataBehaviorActions extends ActoDataSendingActions {

    /**
     * When called from inside a message handler built with an
     * {@link ActoDataCaseFactory}, this method causes the corresponding
     * actor to change its behavior into {@code b}.
     *
     * @param b the behavior
     */
    void changeBehavior(Behavior b);

    /**
     * @return when called before the reception of the {@link GlobalStart}
     * message from the {@link Master} actor, this returns
     * {@link Optional#empty()}. If called after, this returns a
     * {@link MasterInterface} of the master actor which can be used to build
     * and send common kind of messages to the master.
     */
    Optional<MasterInterface> getMasterInterface();

    /**
     * Creates a new actor which on start executes the {@code job} and then
     * dies. The result is used as resolution value for the returned promise.
     *
     * @param job the job to execute
     * @param <T> the type of the result of the job
     * @return a promise that the job will be completed and the result will be
     * available.
     */
    <T> ActoPromise<T> tempWorker(Supplier<T> job);

    /**
     * Wraps an object inside a {@link Data} message, using an instance of
     * {@link CarriedData}.
     *
     * @param object the object to wrap
     * @param <T>    the type of the data
     * @return the wrapped Data message
     */
    <T> Data<T> wrapData(T object);

    /**
     * Unwraps a data message into the resolution value of the returned promise.
     *
     * @param data the data message.
     * @param <T>  the type of the wrapped data
     * @return the promise that the data will be unwrapped and the value will be
     * available.
     */
    <T> ActoPromise<T> unwrapData(Data<T> data);

    /**
     * Outputs a custom text message to the log, preceded by the id of the
     * actor.
     *
     * @param s the message text
     */
    void actorLog(String s);

    /**
     * @return the reference of the actor executing this behavior.
     * @see Behavior#getReference()
     */
    Reference getReference();

    /**
     * Creates a new actor with the specified behavior and returns its
     * reference.
     *
     * @param b the initial behavior of the actor
     * @return the reference of the new actor
     */
    Reference actor(final Behavior b);

}
