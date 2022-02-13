package it.unipr.sowide.actodata.core.controller.content;

import it.unipr.sowide.actodata.core.controller.Controller;
import it.unipr.sowide.actodata.core.dataflow.ContainsReference;
import it.unipr.sowide.actodata.core.engine.Engine;
import it.unipr.sowide.actodes.registry.Reference;

/**
 * Message used to notify a {@link Controller} that it should now control and/or
 * coordinate the specified {@link Engine}.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class AssignEngine implements ContainsReference {
    private final Reference reference;

    /**
     * A new {@link AssignEngine} with the specified reference to the engine.
     *
     * @param reference the reference to the engine.
     */
    public AssignEngine(Reference reference) {
        this.reference = reference;
    }

    /**
     * @return the reference to the engine.
     */
    @Override
    public Reference getReference() {
        return reference;
    }
}
