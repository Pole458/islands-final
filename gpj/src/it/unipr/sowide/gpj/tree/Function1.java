package it.unipr.sowide.gpj.tree;

/**
 * An operation that takes in input the results of two subtrees and computes
 * a result.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public interface Function1<T, C> extends Operation<T, C> {
    /**
     * Evaluates the function using the value and the context in input.
     *
     * @param input   the input value
     * @param context the context
     * @return the result of the evaluation
     */
    T apply(T input, C context);


    @Override
    default int arity() {
        return 1;
    }
}
