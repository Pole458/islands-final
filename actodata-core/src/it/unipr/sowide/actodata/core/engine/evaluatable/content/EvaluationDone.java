package it.unipr.sowide.actodata.core.engine.evaluatable.content;

public class EvaluationDone<T> implements EvaluationOutcome {
    private final T evaluationData;

    public EvaluationDone(T evaluationData) {
        this.evaluationData = evaluationData;
    }

    public T getEvaluationData() {
        return evaluationData;
    }
}
