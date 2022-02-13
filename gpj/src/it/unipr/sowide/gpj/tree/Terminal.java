package it.unipr.sowide.gpj.tree;

/**
 * An operation that has no inputs.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public interface Terminal<T, C> extends Operation<T, C> {
    /**
     * {@inheritDoc}
     **/
    @Override
    default int arity() {
        return 0;
    }

    /**
     * Returns a value when evaluated in the specified context
     *
     * @param context the context
     * @return the result of the evaluation
     */
    T eval(C context);
}
