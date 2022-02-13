package it.unipr.sowide.actodata.core.actodesext;

import it.unipr.sowide.actodata.core.actodesext.promise.ActoPromise;
import it.unipr.sowide.actodes.actor.Behavior;
import it.unipr.sowide.actodes.actor.Message;
import it.unipr.sowide.actodes.actor.MessageHandler;
import it.unipr.sowide.actodes.interaction.Done;
import it.unipr.sowide.actodes.interaction.Error;
import it.unipr.sowide.actodes.interaction.Request;
import it.unipr.sowide.actodes.registry.Reference;

import java.util.List;
import java.util.function.Supplier;

/**
 * An interface containing a set of actions used to send and/or initiate
 * communications with other actors. This interface is mainly meant to expose
 * some functionality of an {@link ActoDataBaseBehavior} to other objects in
 * the implementation of the library.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public interface ActoDataSendingActions {

    /**
     * Sends a specialization of a {@link Request} to an actor to get a
     * particular property (a named value).
     *
     * @param r            the reference of the actor to send the request to
     * @param propertyName the name of the property
     * @param resultType   the expected type
     * @param <R>          the expected type
     * @return a promise for the result value
     * @see ActoDataCaseFactory#serveProperty(String, Supplier)
     */
    <R> ActoPromise<R> queryProperty(
            Reference r,
            String propertyName,
            Class<? extends R> resultType
    );

    /**
     * Sends a specialization of a {@link Request} to an actor to get a
     * particular property (a named value).
     *
     * @param r            the reference of the actor to send the request to
     * @param propertyName the name of the property
     * @param typedNull    the expected type (use {@code (R) null})
     * @param <R>          the expected type
     * @return a promise for the result value
     * @see ActoDataCaseFactory#serveProperty(String, Supplier)
     */
    <R> ActoPromise<R> queryProperty(
            Reference r,
            String propertyName,
            R typedNull
    );

    /**
     * Sends a message to an actor, and registers a dynamic definition in the
     * actor, with a message pattern for a message replying to the original
     * request and content of type {@link T}, and a message handler that
     * resolves the returned {@link ActoPromise} with the content.
     *
     * @param r                    the reference of the actor
     * @param o                    the object which will be used as content for
     *                             the request
     * @param expectedResponseType the expected type of the content of the
     *                             response message
     * @param <T>                  the expected type of the content of the
     *                             response message
     * @return a promise that when compelled, resolves when the response message
     * arrives. If the content of the response message is of wrong type, the
     * promise is rejected with {@link Error#UNEXPECTEDREPLY}.
     */
    <T> ActoPromise<T> promiseFuture(
            Reference r,
            Object o,
            Class<? extends T> expectedResponseType
    );

    /**
     * Sends a message to an actor, and registers a dynamic definition in the
     * actor, with a message pattern for a message replying to the original
     * request and content of type {@link T}, and a message handler that
     * resolves the returned {@link ActoPromise} with the content.
     *
     * @param r         the reference of the actor
     * @param o         the object which will be used as content for the request
     * @param typedNull the expected type of the content of the response message
     *                  (use {@code (T) null})
     * @param <T>       the expected type of the content of the response message
     * @return a promise that when compelled, resolves when the response message
     * arrives. If the content of the response message is of wrong type, the
     * promise is rejected with {@link Error#UNEXPECTEDREPLY}.
     */
    <T> ActoPromise<T> promiseFuture(
            Reference r,
            Object o,
            T typedNull
    );

    /**
     * Sends a message to an actor, and registers a dynamic definition in the
     * actor, with a message pattern for a message replying to the original
     * request and content of type {@link T}, and a message handler that
     * resolves the returned {@link ActoPromise} with the content.
     *
     * @param r   the reference of the actor
     * @param o   the object which will be used as content for the request
     * @param <T> the expected type of the content of the response message
     * @return a promise that when compelled, resolves when the response message
     * arrives. If the content of the response message is of wrong type, the
     * promise is rejected with {@link Error#UNEXPECTEDREPLY}.
     */
    <T> ActoPromise<T> promiseFuture(Reference r, Object o);

    /**
     * Equivalent to {@link #send(Message, Object) send(message, Done.DONE)}.
     *
     * @param message the message to which {@link Done#DONE} is sent to.
     */
    void done(Message message);

    /**
     * @see Behavior#send(Reference, Object)
     */
    void send(final Reference r, final Object c);

    /**
     * @see Behavior#send(List, Object)
     */
    void send(final List<Reference> l, final Object c);

    /**
     * @see Behavior#send(Message, Object)
     */
    void send(final Message m, final Object c);

    /**
     * @see Behavior#future(Message, Object, MessageHandler)
     */
    void future(
            final Reference a,
            final Object c,
            final MessageHandler p
    );

    /**
     * @see Behavior#future(Reference, Object, long, MessageHandler)
     */
    void future(
            final Reference a,
            final Object c,
            final long t,
            final MessageHandler p
    );

    /**
     * @see Behavior#future(Message, Object, MessageHandler)
     */
    void future(
            final Message m,
            final Object c,
            final MessageHandler p
    );

    /**
     * @see Behavior#future(Message, Object, long, MessageHandler)
     */
    void future(
            final Message m,
            final Object c,
            final long t,
            final MessageHandler p
    );
}
