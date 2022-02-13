package it.unipr.sowide.gpj.core.evaluation;

import it.unipr.sowide.gpj.core.Individual;
import it.unipr.sowide.util.promise.Promise;

import java.util.List;

/**
 * Evaluates a population.
 *
 * @param <I> the type of individual
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public interface PopulationEvaluator<I extends Individual> {
    /**
     * Evaluates a population, by updating the fitness values in the
     * individuals.
     *
     * @param population the population to evaluate.
     * @return a promise that resolves with the number of individuals actually
     * evaluated.
     */
    Promise<Integer, Throwable> updateEvaluationsAsync(
            List<I> population
    );
}
