package it.unipr.sowide.gpj.core.breeding.mating;

import it.unipr.sowide.util.Pair;
import it.unipr.sowide.gpj.core.EvolutionContext;
import it.unipr.sowide.gpj.core.Individual;

/**
 * Wraps another operator. Performs the mating only with a specified
 * probability. Otherwise, returns the unchanged parents as children.
 *
 * @param <I> the type of individuals
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class StocasticMateOperator<I extends Individual>
        implements MateOperator<I> {
    private final double rate;
    private final MateOperator<I> delegate;

    /**
     * @param rate     the probability to perform the actual mating
     * @param delegate the actual mating operator excuted
     */
    public StocasticMateOperator(double rate, MateOperator<I> delegate) {
        this.rate = rate;
        this.delegate = delegate;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public Pair<I, I> mate(
            EvolutionContext evolutionContext,
            I parent1,
            I parent2
    ) {
        if (evolutionContext.getRandom().probabilityOutcome(rate)) {
            return delegate.mate(evolutionContext, parent1, parent2);
        } else {
            return Pair.pair(parent1, parent2);
        }
    }
}
