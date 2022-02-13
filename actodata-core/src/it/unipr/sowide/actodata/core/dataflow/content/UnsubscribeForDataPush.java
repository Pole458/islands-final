package it.unipr.sowide.actodata.core.dataflow.content;

import it.unipr.sowide.actodata.core.dataflow.ContainsReference;
import it.unipr.sowide.actodes.interaction.Request;
import it.unipr.sowide.actodes.registry.Reference;

public class UnsubscribeForDataPush implements Request, ContainsReference {
    private final Reference reference;

    public UnsubscribeForDataPush(Reference reference) {
        this.reference = reference;
    }

    @Override
    public Reference getReference() {
        return reference;
    }
}
