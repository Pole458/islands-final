package it.unipr.sowide.gpj.core.breeding.mutation;

import it.unipr.sowide.gpj.core.EvolutionContext;
import it.unipr.sowide.gpj.core.Individual;

/**
 * Mutates an individual into another individual.
 *
 * @param <I> the type of individuals
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public interface MutationOperator<I extends Individual> {
    /**
     * Mutates an individual into another individual.
     *
     * @param evolutionContext the evolution context
     * @param individual       the input individual
     * @return the mutated individual
     */
    I mutate(EvolutionContext evolutionContext, I individual);
}
