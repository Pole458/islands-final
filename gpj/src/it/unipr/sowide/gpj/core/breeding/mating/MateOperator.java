package it.unipr.sowide.gpj.core.breeding.mating;

import it.unipr.sowide.util.Pair;
import it.unipr.sowide.gpj.core.EvolutionContext;
import it.unipr.sowide.gpj.core.Individual;

/**
 * A mate operator is a function that takes in input two individuals and uses
 * them to produce two new individuals.
 *
 * @param <I> the type of individuals
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public interface MateOperator<I extends Individual> {
    /**
     * Mates the two parents into a pair of children
     *
     * @param evolutionContext the context of the evolution
     * @param parent1          the first parent
     * @param parent2          the second parent
     * @return the children in a {@link Pair}
     */
    Pair<I, I> mate(EvolutionContext evolutionContext, I parent1, I parent2);

    /**
     * Mates the two parents into a pair of children
     *
     * @param evolutionContext the context of the evolution
     * @param parents          the pair of parents
     * @return the children in a {@link Pair}
     */
    default Pair<I, I> mate(
            EvolutionContext evolutionContext,
            Pair<I, I> parents
    ) {
        return mate(evolutionContext, parents.get1(), parents.get2());
    }
}
