package it.unipr.sowide.actodata.core.engine.updatable.content;

import it.unipr.sowide.actodata.core.dataflow.Data;
import it.unipr.sowide.actodes.interaction.Request;

public class UpdateByBatch<I> implements Request {
    private final Data<I> batch;

    public UpdateByBatch(Data<I> batch) {
        this.batch = batch;
    }

    public Data<I> getBatch() {
        return batch;
    }
}
