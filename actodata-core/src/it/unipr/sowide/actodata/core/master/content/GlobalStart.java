package it.unipr.sowide.actodata.core.master.content;

import it.unipr.sowide.actodes.interaction.Inform;
import it.unipr.sowide.actodes.registry.Reference;

/**
 * Message used by the master to notify that the ActoDatA application has
 * started.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class GlobalStart implements Inform {
    private final Reference masterReference;

    /**
     * Creates the message.
     *
     * @param masterReference the reference of the master
     */
    public GlobalStart(Reference masterReference) {
        this.masterReference = masterReference;
    }

    /**
     * @return the reference of the master
     */
    public Reference getMasterReference() {
        return masterReference;
    }
}
