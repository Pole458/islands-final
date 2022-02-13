package it.unipr.sowide.actodata.core.engine.trainable.content;

public class StopTraining {
    private final boolean force;

    public StopTraining(boolean force) {
        this.force = force;
    }

    public boolean mustForce() {
        return force;
    }
}
