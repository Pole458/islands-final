package it.unipr.sowide.actodata.core.master;

import it.unipr.sowide.actodata.core.master.content.InformMaster;
import it.unipr.sowide.actodes.actor.Behavior;
import it.unipr.sowide.actodes.registry.Reference;

/**
 * Interface used by the main actors of the ActoDatA application to interact
 * with common protocols with the {@link Master} of the application
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class MasterInterface {
    private final Reference masterReference;
    private final Behavior client;

    /**
     * An interface to the master.
     *
     * @param client          the behavior of the actor using the interface
     * @param masterReference the reference to the master
     */
    public MasterInterface(Behavior client, Reference masterReference) {
        this.masterReference = masterReference;
        this.client = client;
    }

    /**
     * Sends an {@link InformMaster} message to the master.
     *
     * @param event the message content
     * @param <T> the {@link InformMaster} subtype
     */
    public <T extends InformMaster> void informMaster(T event) {
        client.send(masterReference, event);
    }

}
