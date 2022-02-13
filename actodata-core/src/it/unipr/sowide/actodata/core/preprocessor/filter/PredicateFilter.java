package it.unipr.sowide.actodata.core.preprocessor.filter;

import java.util.function.Predicate;

/**
 * A {@link Filter} defined by a delegated {@link Predicate}.
 *
 * @param <T> the input value type
 */
public class PredicateFilter<T> extends Filter<T> {

    private final Predicate<T> predicate;

    /**
     * A {@link Filter} defined by a delegated {@link Predicate}.
     *
     * @param predicate the predicate
     */
    public PredicateFilter(Predicate<T> predicate) {
        this.predicate = predicate;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public boolean test(T t) {
        return predicate.test(t);
    }
}
