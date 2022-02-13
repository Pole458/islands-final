package it.unipr.sowide.gpj.core.breeding;

import it.unipr.sowide.gpj.core.EvolutionContext;
import it.unipr.sowide.gpj.core.Individual;
import it.unipr.sowide.util.promise.Promise;

import java.util.List;

/**
 * Method used to transform a population at the end of a generation.
 *
 * @param <I> the type of individuals
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public interface BreedingStrategy<I extends Individual> {
    /**
     * Transforms a population.
     *
     * @param evolutionContext the evolution context in which the breeding is
     *                         happening
     * @param oldPopulation    the population to be transformed
     * @param targetPopSize    the expected size of the final population
     * @return a promise that resolves to the new, transformed, population.
     */
    Promise<List<I>, Throwable> breed(
            EvolutionContext evolutionContext,
            List<I> oldPopulation,
            int targetPopSize
    );
}
