package it.unipr.sowide.actodata.core.actodesext;

import it.unipr.sowide.actodes.actor.Behavior;
import it.unipr.sowide.actodes.actor.CaseFactory;
import it.unipr.sowide.actodes.actor.Message;
import it.unipr.sowide.actodes.actor.MessageHandler;
import it.unipr.sowide.actodes.actor.MessagePattern;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Interface containing methods of a {@link ActoDataCaseFactory} that
 * represents some common case building methods. This interface is mainly meant
 * to expose some configuration functionality of an {@link ActoDataBaseBehavior}
 * to other objects in the implementation of the library.
 */
public interface IActoDataCaseFactory {
    /**
     * Builds a case that handles the reception of a message of type
     * {@code type} with a function that takes in input the content (casted
     * -after a check- to type {@link T}) of the message and the message, and
     * returns the new Behavior (or null).
     *
     * @param type the expected type of the message
     * @param h    the <i>content and message</i> handler
     * @param <T>  the expected type of the message
     */
    <T> void defineForContentOfType(
            Class<? extends T> type,
            BiFunction<T, Message, Behavior> h
    );

    /**
     * Builds a case that handles the reception of a message of type
     * {@code type} with a consumer that takes in input the content (casted
     * -after a check- to type {@link T}) of the message and the message.
     * <p></p>
     * Note that this in ActoDeS's
     * {@link CaseFactory#define(MessagePattern, MessageHandler)} is translated
     * into a message handler that retunrs null as new behavior.
     *
     * @param type the expected type of the message
     * @param h    the <i>content and message</i> handler
     * @param <T>  the expected type of the message
     */
    <T> void onContentOfType(Class<? extends T> type, BiConsumer<T, Message> h);

    /**
     * "Serves" a value identified by a static name. Other actors can retrieve
     * the value by means of the {@link ActoDataSendingActions#queryProperty}
     * methods.
     *
     * @param propertyName the name of the property
     * @param getter       the methods that computes the value of the property
     * @param <R>          the type of the property
     */
    <R> void serveProperty(String propertyName, Supplier<? extends R> getter);

    /**
     * Builds a case that handles the reception of a message that matches the
     * specified {@link MessagePattern} with the {@link Consumer} {@code h}.
     *
     * @param p the pattern
     * @param h the consumer
     */
    void on(MessagePattern p, Consumer<Message> h);

    /**
     * @see CaseFactory#define(MessagePattern, MessageHandler)
     */
    void define(MessagePattern p, MessageHandler h);

    /**
     * Used by some derived interfaces/classes to perform actions.
     *
     * @return the {@link ActoDataBaseBehavior} being configured with this case
     * factory.
     */
    ActoDataBaseBehavior self();
}
