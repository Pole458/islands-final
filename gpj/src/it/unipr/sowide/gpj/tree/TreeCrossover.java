package it.unipr.sowide.gpj.tree;

import it.unipr.sowide.util.Pair;
import it.unipr.sowide.gpj.core.EvolutionContext;
import it.unipr.sowide.gpj.core.breeding.mating.MateOperator;

/**
 * A {@link MateOperator} that performs a crossover between two trees.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class TreeCrossover<T, C> implements MateOperator<Tree<T, C>> {

    private final int maxResultHeight;

    /**
     * @param maxResultHeight the maximum height of the tree
     */
    public TreeCrossover(int maxResultHeight) {
        this.maxResultHeight = maxResultHeight;
    }

    @Override
    public Pair<Tree<T, C>, Tree<T, C>> mate(
            EvolutionContext evolutionContext,
            Tree<T, C> parent1,
            Tree<T, C> parent2
    ) {
        return Tree.crossover(
                evolutionContext.getRandom(), parent1, parent2, maxResultHeight
        );
    }
}
