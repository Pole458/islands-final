package it.unipr.sowide.gpj.tree;

/**
 * An operation of a {@link Tree} represent a particular step in the evaluation
 * of the tree itself.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public interface Operation<T, C> {
    /**
     * The name of the operation
     *
     * @return the name
     */
    String getName();

    /**
     * @return the arity of the operation (how many subtrees accepts in input?)
     */
    int arity();

    /**
     * Transformation of this operation that is invoked the first time the tree
     * is evaluated.
     */
    Operation<T, C> transformInit();

    /**
     * @return a copy of this operation.
     */
    Operation<T, C> copy();
}
