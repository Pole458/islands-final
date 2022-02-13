package it.unipr.sowide.actodata.core.engine.updatable.content;

import it.unipr.sowide.actodata.core.dataflow.Data;
import it.unipr.sowide.actodes.interaction.Request;

public class UpdateByInstance<I> implements Request {
    private final Data<I> instance;

    public UpdateByInstance(Data<I> instance) {
        this.instance = instance;
    }

    public Data<I> getInstance() {
        return instance;
    }
}
