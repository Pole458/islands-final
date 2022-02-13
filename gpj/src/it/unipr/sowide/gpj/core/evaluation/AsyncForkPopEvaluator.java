package it.unipr.sowide.gpj.core.evaluation;

import it.unipr.sowide.gpj.core.Individual;
import it.unipr.sowide.util.Pair;
import it.unipr.sowide.util.promise.Promise;
import it.unipr.sowide.util.promise.Promises;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Updates the fitnesses of a population in a non-blocking way, using the
 * fork & join paradigm for parallelism.
 *
 * @param <I> the type of individual
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class AsyncForkPopEvaluator<I extends Individual>
        implements PopulationEvaluator<I> {

    private final AsyncIndividualEvaluator<I> individualEvaluator;

    /**
     * Updates the fitnesses of a population.
     *
     * @param individualEvaluator the operation used to compute the fitness
     *                            of a single individual
     */
    public AsyncForkPopEvaluator(AsyncIndividualEvaluator<I> individualEvaluator) {
        this.individualEvaluator = individualEvaluator;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public Promise<Integer, Throwable> updateEvaluationsAsync(
            List<I> population
    ) {
        return Promises.allSettled(population.stream()
                .map(individual -> {
                    return individualEvaluator.evaluate(individual)
                            .map((fitness) -> Pair.pair(individual, fitness));
                }).collect(Collectors.toList())
        ).map((pairs) -> {
            for (Pair<I, Double> pair : pairs) {
                pair.get1().setFitness(pair.get2());
            }
            return pairs.size();
        }).mapError((errors) -> {
            if (errors.isEmpty()) {
                return Optional.empty();
            } else {
                return Optional.of(errors.get(0));
            }
        });
    }
}
