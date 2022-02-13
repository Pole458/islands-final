package it.unipr.sowide.gpj.core.evaluation;

import it.unipr.sowide.gpj.core.Individual;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Updates the fitnesses in a population without using parallelism.
 *
 * @param <I> the type of individual.
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 * @implSpec note that this is not parallel but guarantees determinism with the
 * same random seed.
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class SyncSequentialPopEvaluator<I extends Individual>
        implements SyncPopulationEvaluator<I> {

    private final SyncIndividualEvaluator<I> evaluator;

    /**
     * Updates the fitnesses in a population
     *
     * @param evaluator method used to compute the fitness of each individual
     */
    public SyncSequentialPopEvaluator(SyncIndividualEvaluator<I> evaluator) {
        this.evaluator = evaluator;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public int updateEvaluations(List<I> population) {
        final AtomicInteger evaluationsCount = new AtomicInteger();
        population.forEach(i -> {
            if (i.getFitness().isEmpty()) {
                i.setFitness(evaluator.evaluate(i));
                evaluationsCount.incrementAndGet();
            }
        });
        return evaluationsCount.get();
    }
}
