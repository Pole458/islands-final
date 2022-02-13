package it.unipr.sowide.gpj.tree;

import it.unipr.sowide.gpj.core.IndividualGenerator;
import it.unipr.sowide.util.RandomUtils;

import java.util.List;

/**
 * An {@link IndividualGenerator} that generates a tree.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class RandomTreeGenerator<T, C>
        implements IndividualGenerator<Tree<T, C>> {

    private final RandomUtils random;
    private final int minHeight;
    private final int maxHeight;
    private final List<Operation<T, C>> operationSet;

    /**
     * A random tree generator
     *
     * @param random       the random generation utility
     * @param minHeight    the minimum height of the resulting trees
     * @param maxHeight    the maximum height of the resulting trees
     * @param operationSet the set of the operations used to build the tree
     */
    public RandomTreeGenerator(
            RandomUtils random,
            int minHeight,
            int maxHeight,
            List<Operation<T, C>> operationSet
    ) {
        this.random = random;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.operationSet = operationSet;
    }

    @Override
    public Tree<T, C> generateIndividual() {
        Tree<T, C> result = null;
        while (result == null) {
            TreeInitMethod method;
            if (random.nextBoolean()) {
                method = TreeInitMethod.FULL;
            } else {
                method = TreeInitMethod.GROW;
            }
            Tree<T, C> e = Tree.randomTree(
                    random,
                    operationSet,
                    random.nextInt(maxHeight - minHeight) + minHeight,
                    method
            );
            if (e.height() >= minHeight) {
                result = e;
            }
        }
        return result;
    }


}
