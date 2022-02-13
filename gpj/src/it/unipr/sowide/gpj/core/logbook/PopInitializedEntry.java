package it.unipr.sowide.gpj.core.logbook;

public class PopInitializedEntry extends EvolutionLogbookEntry {
    public PopInitializedEntry(int gen, int populationSize) {
        super(gen, "Population initialized (" + populationSize + ")");
    }
}
