package it.unipr.sowide.actodata.core.controller;

import it.unipr.sowide.actodata.core.dataflow.Data;
import it.unipr.sowide.actodata.core.dataflow.DataTicket;
import it.unipr.sowide.actodata.core.actodesext.ActoDataCaseFactory;
import it.unipr.sowide.util.CheckedCast;
import it.unipr.sowide.actodes.actor.Message;
import it.unipr.sowide.util.annotations.PackagePrivate;

import java.util.function.BiConsumer;

/**
 * A specialization of {@link ActoDataCaseFactory} that adds some case building
 * methods specific for the {@link Controller} behavior.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class ControllerCaseFactory
        extends ActoDataCaseFactory
        implements IControllerCaseFactory {

    private final ControllerInputDataDispatcher dispacher;

    /**
     * A case factory for the {@code behavior} being configured, with the
     * {@code delegate}d base {@link ActoDataCaseFactory} and a dispatcher
     * used to manage particular cases of {@link Data} messages.
     *
     * @param behavior  the behavior being configured
     * @param delegate  the delegate factory
     * @param dispacher the input data dispatcher
     */
    @PackagePrivate
    ControllerCaseFactory(
            Controller behavior,
            ActoDataCaseFactory delegate,
            ControllerInputDataDispatcher dispacher
    ) {
        super(behavior, delegate);
        this.dispacher = dispacher;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public <T> void onData(BiConsumer<T, Message> whatToDo) {
        dispacher.addOnCarriedDataAcceptor(whatToDo);
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public <T> void onData(Class<? extends T> type, BiConsumer<T, Message> whatToDo) {
        dispacher.addOnCarriedDataAcceptor(type, whatToDo);
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public void onTicket(String typeName, BiConsumer<DataTicket<?, ?>, Message> whatToDo) {
        dispacher.addOnDataTicketAcceptor(typeName, whatToDo);
    }


    /**
     * {@inheritDoc}
     **/
    @Override
    public <T> void onTicket(Class<? extends T> type, BiConsumer<DataTicket<T, ?>, Message> whatToDo) {
        dispacher.addOnDataTicketAcceptor(
                type.getName(),
                (dt, message) -> {
                    CheckedCast.cast((DataTicket<T, ?>) null, dt)
                            .ifSuccessful(typedDt -> {
                                whatToDo.accept(typedDt, message);
                            });
                }
        );
    }
}
