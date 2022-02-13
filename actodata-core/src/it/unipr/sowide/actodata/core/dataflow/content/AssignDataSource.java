package it.unipr.sowide.actodata.core.dataflow.content;

import it.unipr.sowide.actodes.interaction.Request;
import it.unipr.sowide.actodes.registry.Reference;

public class AssignDataSource implements Request {
    private final Reference sourceReference;

    public AssignDataSource(Reference sourceReference) {
        this.sourceReference = sourceReference;
    }

    public Reference getSourceReference() {
        return sourceReference;
    }


    public Reference getReference() {
        return sourceReference;
    }
}
