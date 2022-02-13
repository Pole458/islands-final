package it.unipr.sowide.actodata.core.dataset.content;

import it.unipr.sowide.actodes.interaction.Response;
import it.unipr.sowide.actodes.registry.Reference;

/**
 * Message that signals that the requested acquirer has been spawned.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class AcquirerSpawned implements Response {
    private final Reference acquirerReference;

    public AcquirerSpawned(Reference acquirerReference) {
        this.acquirerReference = acquirerReference;
    }

    /**
     * @return the reference of the acquirer
     */
    public Reference getAcquirerReference() {
        return acquirerReference;
    }
}
