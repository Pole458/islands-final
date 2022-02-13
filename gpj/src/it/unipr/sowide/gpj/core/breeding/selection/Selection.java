package it.unipr.sowide.gpj.core.breeding.selection;

import it.unipr.sowide.gpj.core.EvolutionContext;
import it.unipr.sowide.gpj.core.Individual;
import it.unipr.sowide.gpj.core.breeding.BreedingStrategy;
import it.unipr.sowide.util.promise.Promise;

import java.util.List;

/**
 * A breeding strategy that
 * @param <I>
 */
public abstract class Selection<I extends Individual>
        implements BreedingStrategy<I> {
    @Override
    public Promise<List<I>, Throwable> breed(
            EvolutionContext evolutionContext,
            List<I> oldPopulation,
            int targetPopSize
    ) {
        return select(
                evolutionContext,
                oldPopulation,
                targetPopSize
        );
    }

    public abstract Promise<List<I>, Throwable> select(
            EvolutionContext evolutionContext,
            List<I> oldPopulation,
            int targetPopSize
    );
}
