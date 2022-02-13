package it.unipr.sowide.actodata.core.dataflow;

import it.unipr.sowide.actodes.actor.Message;
import it.unipr.sowide.actodes.registry.Reference;

/**
 * A partial specialization of {@link PushReceiver} for the reception of {@link Data}
 * messages.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public abstract class DataReceiver extends PushReceiver<Data<?>> {

    /**
     * A new {@link DataReceiver}.
     */
    @SuppressWarnings({"unchecked", "RedundantCast"})
    public DataReceiver() {
        super((Class<? extends Data<?>>) (Class<?>) Data.class);
        this.setOnDataHandler(this::onData);
    }


    /**
     * Method that defines what to do when a new {@link Data} message arrives.
     *
     * @param tData   the content of the message
     * @param message the message
     */
    protected abstract void onData(Data<?> tData, Message message);


    /** {@inheritDoc} **/
    @Override
    public void addRegisteredSender(Reference reference) {
        this.getRegisteredSenders().add(reference);
    }
}
