package it.unipr.sowide.actodata.core.engine.query.content;

import it.unipr.sowide.actodata.core.dataflow.Data;
import it.unipr.sowide.actodes.interaction.Request;

public class EngineQuery<I> implements Request {
    private final Data<I> queryInput;


    public EngineQuery(Data<I> queryInput) {
        this.queryInput = queryInput;
    }

    public Data<I> getQueryInput() {
        return queryInput;
    }
}
