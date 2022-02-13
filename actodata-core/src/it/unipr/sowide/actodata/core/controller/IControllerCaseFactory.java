package it.unipr.sowide.actodata.core.controller;

import it.unipr.sowide.actodata.core.dataflow.CarriedData;
import it.unipr.sowide.actodata.core.dataflow.DataTicket;
import it.unipr.sowide.actodes.actor.Message;

import java.util.function.BiConsumer;

/**
 * Exposes some operations of a {@link ControllerCaseFactory}.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public interface IControllerCaseFactory {
    /**
     * Defines a message handler for a message of type {@link CarriedData}, in
     * which the data is of type {@link T}.
     *
     * @param whatToDo a {@link BiConsumer} accepting the unwrapped data
     *                 instance and the message
     * @param <T>      the expected type of the unwrapped data instance
     */
    <T> void onData(BiConsumer<T, Message> whatToDo);

    /**
     * Defines a message handler for a message of type {@link CarriedData}, in
     * which the data is of type {@link T} and is an instance of the provided
     * class.
     *
     * @param type     the type of the unwrapped data
     * @param whatToDo a {@link BiConsumer} accepting the unwrapped data
     *                 instance and the message
     * @param <T>      the expected type of the unwrapped data instance
     */
    <T> void onData(Class<? extends T> type, BiConsumer<T, Message> whatToDo);

    /**
     * Defines a message haandler for a message of type {@link DataTicket}.
     *
     * @param typeName the string describing the type of the data behind the
     *                 ticket
     * @param whatToDo a {@link BiConsumer} accepting the retrieved data
     *                 instance and the message
     */
    void onTicket(
            String typeName,
            BiConsumer<DataTicket<?, ?>, Message> whatToDo
    );

    /**
     * Defines a message haandler for a message of type {@link DataTicket}.
     *
     * @param type     the class of which the data behind the ticket is an
     *                 instance
     * @param whatToDo a {@link BiConsumer} accepting the retrieved data
     *                 instance and the message
     */
    <T> void onTicket(
            Class<? extends T> type,
            BiConsumer<DataTicket<T, ?>, Message> whatToDo
    );
}
