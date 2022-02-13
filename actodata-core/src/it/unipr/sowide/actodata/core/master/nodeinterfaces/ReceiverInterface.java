package it.unipr.sowide.actodata.core.master.nodeinterfaces;

import it.unipr.sowide.actodata.core.dataflow.ContainsReference;
import it.unipr.sowide.actodata.core.dataflow.content.AssignDataSource;
import it.unipr.sowide.actodata.core.master.Master;

/**
 * Used to extend a {@link NodeInterface} with typical interaction operations
 * for actors that are supposed to receive data from other actors.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public interface ReceiverInterface extends ContainsReference {
    /**
     * @return an instance of the master using this interface, for delegation
     * in default methods only.
     */
    Master master();

    /**
     * Asks the actor behind this interface to initiate the protocol used to
     * receive data messages from the actor behind the provided interface.
     *
     * @param senderInterface the interface of the sender of data
     */
    default void addSource(
            SenderInterface senderInterface
    ) {
        master().send(
                getReference(),
                new AssignDataSource(senderInterface.getReference())
        );
    }


}
