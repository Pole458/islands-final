package it.unipr.sowide.islands.migration.graph;

public class Arc {

    public final int originIndex;
    public final int destinationIndex;

    public Arc(int originIndex, int destinationIndex) {
        this.originIndex = originIndex;
        this.destinationIndex = destinationIndex;
    }
}
