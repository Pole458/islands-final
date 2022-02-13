package it.unipr.sowide.gpj.core.breeding.mutation;

import it.unipr.sowide.gpj.core.EvolutionContext;
import it.unipr.sowide.gpj.core.Individual;

/**
 * Wraps another mutation operator. Performs the mutation only with a specified
 * probability.
 *
 * @param <I> the type of individuals
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class StocasticMutationOperator<I extends Individual>
        implements MutationOperator<I> {
    private final double probability;
    private final MutationOperator<I> delegate;

    /**
     * @param probability the probability at which the mutation occurs
     * @param delegate    the actual mutation
     */
    public StocasticMutationOperator(
            double probability,
            MutationOperator<I> delegate
    ) {
        this.probability = probability;
        this.delegate = delegate;
    }

    @Override
    public I mutate(EvolutionContext evolutionContext, I individual) {
        if (evolutionContext.getRandom().probabilityOutcome(probability)) {
            return delegate.mutate(evolutionContext, individual);
        } else {
            return individual;
        }
    }
}
