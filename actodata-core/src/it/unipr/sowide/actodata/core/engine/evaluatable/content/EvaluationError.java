package it.unipr.sowide.actodata.core.engine.evaluatable.content;

public class EvaluationError implements EvaluationOutcome {
    private final String message;

    public EvaluationError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
