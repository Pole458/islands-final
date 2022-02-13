package it.unipr.sowide.gpj.core.logbook;

import it.unipr.sowide.gpj.core.Individual;

public class GenerationBestIndividualEntry extends EvolutionLogbookEntry {
    public GenerationBestIndividualEntry(Individual individual, int gen) {
        super(gen, "Current generation best individual (gen "+individual.getBirthGeneration()+"):" +
                " \n"+individual+"\n" +
                "Best individual fitness: "+
                (individual.getFitness().isPresent()? individual.getFitness().get():"(not computed)")
                +"\n");
    }
}
