package it.unipr.sowide.gpj.core.logbook;

import it.unipr.sowide.gpj.core.Individual;

public class TargetFitnessReachedEntry extends EvolutionLogbookEntry {
    public TargetFitnessReachedEntry(Individual currentBest, int generationCount) {
        super(generationCount, "Evolution terminated because target fitness was reached. " +
                "Best Individual:\n" + currentBest);
    }
}
