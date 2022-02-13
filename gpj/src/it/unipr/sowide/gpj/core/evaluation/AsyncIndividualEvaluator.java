package it.unipr.sowide.gpj.core.evaluation;

import it.unipr.sowide.gpj.core.Individual;
import it.unipr.sowide.util.promise.Promise;

/**
 * Evaluates an individual in a non-blocking way.
 *
 * @param <I> the type of individual
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public interface AsyncIndividualEvaluator<I extends Individual> {
    /**
     * Evaluates an individual.
     *
     * @param individual the individual
     * @return a promise that resolves to the fitness of the individual.
     */
    Promise<Double, Throwable> evaluate(I individual);
}
