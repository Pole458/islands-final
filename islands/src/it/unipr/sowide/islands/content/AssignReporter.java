package it.unipr.sowide.islands.content;

import it.unipr.sowide.actodata.core.dataflow.ContainsReference;
import it.unipr.sowide.actodes.interaction.Request;
import it.unipr.sowide.actodes.registry.Reference;

/**
 * Method used by the Master to request a controller to connect its output
 * to the specified reporter.
 *
 */
public class AssignReporter implements ContainsReference, Request {
    private final Reference reference;

    /**
     * @param reference the reference of the reporter
     */
    public AssignReporter(Reference reference) {
        this.reference = reference;
    }

    /**
     * @return the reference of the reporter
     */
    @Override
    public Reference getReference() {
        return reference;
    }
}
