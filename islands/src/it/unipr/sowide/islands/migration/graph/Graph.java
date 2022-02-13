package it.unipr.sowide.islands.migration.graph;

import java.util.List;

public class Graph {
    private final List<Arc> arcs;

    public Graph(List<Arc> arcs) {
        this.arcs = arcs;
    }

    public List<Arc> getArcs() {
        return arcs;
    }
}
