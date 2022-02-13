package it.unipr.sowide.gpj.core.evaluation;

import it.unipr.sowide.gpj.core.Individual;
import it.unipr.sowide.util.promise.Promise;
import it.unipr.sowide.util.promise.Promises;

import java.util.List;

/**
 * Evaluates the individuals in a blocking call.
 *
 * @param <I> the type of individual
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public interface SyncPopulationEvaluator<I extends Individual>
        extends PopulationEvaluator<I> {
    /** {@inheritDoc} **/
    @Override
    default Promise<Integer, Throwable> updateEvaluationsAsync(
            List<I> population
    ) {
        return Promises.immediatelyResolve(updateEvaluations(population));
    }

    /**
     * Evaluates a population, by updating the fitness values in the
     * individuals.
     *
     * @param population the population to evaluate.
     * @return the number of individuals actually evaluated.
     */
    int updateEvaluations(List<I> population);
}
