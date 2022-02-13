package it.unipr.sowide.actodata.core.controller.content;

import it.unipr.sowide.actodata.core.controller.Controller;
import it.unipr.sowide.actodata.core.dataflow.ContainsReference;
import it.unipr.sowide.actodata.core.engine.Engine;
import it.unipr.sowide.actodes.interaction.Request;
import it.unipr.sowide.actodes.registry.Reference;

/**
 * Method used by {@link Controller}s to register to {@link Engine}s for
 * notification of relevant events.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class SubscribeEngineController implements ContainsReference, Request {
    private final Reference reference;

    /**
     * A new message with the reference to the controller
     *
     * @param reference the reference to the controller
     */
    public SubscribeEngineController(Reference reference) {
        this.reference = reference;
    }

    /**
     * @return the reference to the controller
     */
    @Override
    public Reference getReference() {
        return reference;
    }
}
