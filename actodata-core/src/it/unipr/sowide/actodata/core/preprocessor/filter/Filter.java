package it.unipr.sowide.actodata.core.preprocessor.filter;

import it.unipr.sowide.actodata.core.preprocessor.Preprocessor;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A {@link Preprocessor} that outputs input values, without performing
 * transformations on them, only if {@link #test(Object)} returns true on them.
 *
 * @param <T> the input value type
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public abstract class Filter<T>
        extends Preprocessor<T, T> implements Predicate<T> {
    /**
     * {@inheritDoc}
     **/
    @Override
    public void preprocess(T input, Consumer<T> resultAcceptor) {
        if (this.test(input)) {
            resultAcceptor.accept(input);
        }
    }
}
