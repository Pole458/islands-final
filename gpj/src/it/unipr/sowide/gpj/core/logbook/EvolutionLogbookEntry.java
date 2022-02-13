package it.unipr.sowide.gpj.core.logbook;

public class EvolutionLogbookEntry {
    private final int gen;
    private final String description;

    public EvolutionLogbookEntry(int gen, String description) {
        this.gen = gen;
        this.description = description;
    }

    public int getGeneration() {
        return gen;
    }

    public String getDescription() {
        return description;
    }


    public String toOutput() {
        return "GEN " + gen + ": " + description;
    }
}
