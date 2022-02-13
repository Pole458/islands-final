package it.unipr.sowide.actodata.core.controller;

import it.unipr.sowide.actodata.core.dataflow.DataReceiver;
import it.unipr.sowide.actodata.core.dataflow.CarriedData;
import it.unipr.sowide.actodata.core.dataflow.Data;
import it.unipr.sowide.actodata.core.dataflow.DataTicket;
import it.unipr.sowide.actodata.core.actodesext.ActoDataBaseBehavior;
import it.unipr.sowide.util.BagMap;
import it.unipr.sowide.util.CheckedCast;
import it.unipr.sowide.actodes.actor.Message;
import it.unipr.sowide.util.annotations.PackagePrivate;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * A specialization of a {@link DataReceiver} behavior fragment, used by the
 * controllers to define and handle reception of {@link Data} messages.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
@PackagePrivate
class ControllerInputDataDispatcher extends DataReceiver {

    private final List<BiConsumer<Object, Message>> onCarriedDefinitions
            = new ArrayList<>();

    private final BagMap<String, BiConsumer<DataTicket<?, ?>, Message>> onTicketDefinitions
            = new BagMap<>();

    private final ActoDataBaseBehavior behavior;


    @PackagePrivate
    ControllerInputDataDispatcher(ActoDataBaseBehavior behavior) {
        super();
        this.behavior = behavior;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    protected final void onData(Data<?> data, Message message) {
        if (data instanceof CarriedData<?>) {
            this.dispatchCarried((CarriedData<?>) data, message);
        } else if (data instanceof DataTicket<?, ?>) {
            this.dispatchTicket((DataTicket<?, ?>) data, message);
        }
    }

    private void dispatchCarried(CarriedData<?> data, Message message) {
        for (BiConsumer<Object, Message> dispatch : onCarriedDefinitions) {
            data.getData(behavior)
                    .then(x -> dispatch.accept(x, message))
                    .compel();
        }
    }

    private void dispatchTicket(
            DataTicket<?, ?> data,
            Message message
    ) {
        for (var acceptor : onTicketDefinitions.get(data.getTypeName())) {
            acceptor.accept(data, message);
        }
    }

    @PackagePrivate
    <T> void addOnCarriedDataAcceptor(BiConsumer<T, Message> acceptor) {
        onCarriedDefinitions.add((object, message) -> {
            CheckedCast.cast((T) null, object).ifSuccessful(t -> {
                acceptor.accept(t, message);
            });
        });
    }

    @PackagePrivate
    <T> void addOnCarriedDataAcceptor(
            Class<? extends T> type,
            BiConsumer<T, Message> acceptor
    ) {
        onCarriedDefinitions.add((object, message) -> {
            CheckedCast.cast(type, object).ifPresent(o -> {
                acceptor.accept(o, message);
            });
        });
    }

    @PackagePrivate
    void addOnDataTicketAcceptor(
            String typeName,
            BiConsumer<DataTicket<?, ?>, Message> acceptor
    ) {
        onTicketDefinitions.putElement(typeName, acceptor);
    }


}
