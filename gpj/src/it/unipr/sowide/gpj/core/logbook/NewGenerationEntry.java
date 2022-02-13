package it.unipr.sowide.gpj.core.logbook;

public class NewGenerationEntry extends EvolutionLogbookEntry {
    public NewGenerationEntry(int gen, int currentPopSize) {
        super(gen, "New generation: " + gen +"; " +
                "pop size: "+(currentPopSize>=0?""+currentPopSize:"???"));
    }
}
