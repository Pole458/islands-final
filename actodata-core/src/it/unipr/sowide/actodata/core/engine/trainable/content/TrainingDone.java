package it.unipr.sowide.actodata.core.engine.trainable.content;

public class TrainingDone implements TrainingOutcome {
    public static final TrainingDone INSTANCE = new TrainingDone();


    @Override
    public String toString() {
        return "TrainingDone";
    }
}
