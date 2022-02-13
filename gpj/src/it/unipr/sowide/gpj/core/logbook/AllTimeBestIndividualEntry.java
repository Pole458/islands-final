package it.unipr.sowide.gpj.core.logbook;

import it.unipr.sowide.gpj.core.Individual;

public class AllTimeBestIndividualEntry extends EvolutionLogbookEntry {
    public AllTimeBestIndividualEntry(Individual individual, int gen) {
        super(gen, "Current all-time best individual (gen "+individual.getBirthGeneration()+"):" +
                " \n"+individual+"\n" +
                "Best individual fitness: "
                + (individual.getFitness().isPresent()? individual.getFitness().get():"(not computed)") +"\n");
    }
}
