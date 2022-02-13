package it.unipr.sowide.actodata.core.engine.evaluatable.content;

import it.unipr.sowide.actodata.core.dataflow.Data;
import it.unipr.sowide.actodes.interaction.Request;

public class EvaluationRequest<EI> implements Request {
    private final Data<EI> testSet;

    public EvaluationRequest(Data<EI> testSet) {
        this.testSet = testSet;
    }

    public Data<EI> getTestSet() {
        return testSet;
    }
}
