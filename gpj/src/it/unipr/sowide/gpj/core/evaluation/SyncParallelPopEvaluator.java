package it.unipr.sowide.gpj.core.evaluation;

import it.unipr.sowide.gpj.core.Individual;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Updates the fitnesses of a population with a blocking call, using
 * multithreading parallelism.
 *
 * @param <I> the type of the individual
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class SyncParallelPopEvaluator<I extends Individual>
        implements SyncPopulationEvaluator<I> {

    private final SyncIndividualEvaluator<I> evaluator;

    /**
     * Updates the fitnesses of a population.
     *
     * @param evaluator the method used to compute the fitness of each
     *                  individual
     */
    public SyncParallelPopEvaluator(SyncIndividualEvaluator<I> evaluator) {
        this.evaluator = evaluator;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public int updateEvaluations(List<I> population) {
        final AtomicInteger evaluationsCount = new AtomicInteger();
        population.stream()
                .parallel()
                .forEach(i -> {
                    if (i.getFitness().isEmpty()) {
                        i.setFitness(evaluator.evaluate(i));
                        evaluationsCount.incrementAndGet();
                    }
                });
        return evaluationsCount.get();
    }
}
