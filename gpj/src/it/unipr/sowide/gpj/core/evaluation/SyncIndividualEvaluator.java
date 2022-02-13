package it.unipr.sowide.gpj.core.evaluation;

import it.unipr.sowide.gpj.core.Individual;

/**
 * Evaluates an individual with a blocking call.
 *
 * @param <I> the type of individual
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public interface SyncIndividualEvaluator<I extends Individual> {
    /**
     * Evaluates an individual.
     *
     * @param individual the individual
     * @return the fitness value
     */
    double evaluate(I individual);
}
