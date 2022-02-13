package it.unipr.sowide.gpj.core.breeding;

import it.unipr.sowide.gpj.core.EvolutionContext;
import it.unipr.sowide.gpj.core.Individual;
import it.unipr.sowide.util.promise.Promise;
import it.unipr.sowide.util.promise.Promises;

import java.util.HashSet;
import java.util.List;

/**
 * Wraps another breeding strategy. At the end of the breeding, "kills"
 * (using {@link Individual#kill()}) all the individuals in the old population
 * that are not present in the new population.
 *
 * @param <I> the type of individuals
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class KillOldIndividuals<I extends Individual>
        implements BreedingStrategy<I> {

    private final BreedingStrategy<I> delegate;

    /**
     * @param delegate the wrapped breeding strategy
     */
    public KillOldIndividuals(BreedingStrategy<I> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Promise<List<I>, Throwable> breed(
            EvolutionContext evolutionContext,
            List<I> oldPopulation,
            int targetPopSize
    ) {
        return Promises.Do(
        ).thenAwait((__) -> delegate.breed(
                evolutionContext,
                oldPopulation,
                targetPopSize
        )).then((breed) -> {
            HashSet<I> set = new HashSet<>(breed);
            long count = oldPopulation.stream()
                    .filter(i -> !set.contains(i))
                    .peek(Individual::kill)
                    .count();
            evolutionContext.putStatistic("individuals_killed", (double) count);
        });
    }
}
