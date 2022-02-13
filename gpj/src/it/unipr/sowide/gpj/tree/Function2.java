package it.unipr.sowide.gpj.tree;

/**
 * An operation that takes in input the results of two subtrees and computes
 * a result.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public interface Function2<T, C> extends Operation<T, C> {
    /**
     * Evaluates the function using the two values and the context in input.
     *
     * @param t1      the first value
     * @param t2      the second value
     * @param context the context
     * @return the result of the evaluation
     */
    T apply(T t1, T t2, C context);

    @Override
    default int arity() {
        return 2;
    }

}
