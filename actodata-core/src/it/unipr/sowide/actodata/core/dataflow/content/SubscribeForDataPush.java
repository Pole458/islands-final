package it.unipr.sowide.actodata.core.dataflow.content;

import it.unipr.sowide.actodata.core.dataflow.ContainsReference;
import it.unipr.sowide.actodes.interaction.Request;
import it.unipr.sowide.actodes.registry.Reference;

public class SubscribeForDataPush implements Request, ContainsReference {
    private final Reference subscriber;

    public SubscribeForDataPush(Reference subscriber) {
        this.subscriber = subscriber;
    }

    public Reference getSubscriber() {
        return subscriber;
    }

    @Override
    public Reference getReference() {
        return subscriber;
    }
}
