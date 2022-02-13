package it.unipr.sowide.actodata.core.engine.trainable.content;

public class TrainingError implements TrainingOutcome {
    private final String message;

    public TrainingError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
