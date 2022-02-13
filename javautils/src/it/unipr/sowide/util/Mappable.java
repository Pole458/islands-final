package it.unipr.sowide.util;

import it.unipr.sowide.util.annotations.Immutable;

import java.util.function.Function;

/**
 * A Mappable is an immutable value that can be transformed by means of a
 * functional {@link Mappable#map(Function)} operation.
 *
 * @param <X> the wrapped value type
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
@Immutable
public interface Mappable<X> {
    /**
     * Returns a new mappable that wraps a value of type {@link R}.
     * The transformation of the wrapped value is performed by means of the
     * application of the specified {@code function}.
     *
     * @param function the function that transforms the wrapped value
     * @param <R> the type of value returned by the function
     * @return a new mappable that wraps a value of type {@link R}.
     * The transformation of the wrapped value is performed by means of the
     * application of the specified {@code function}.
     */
    <R> Mappable<R> map(Function<X, R> function);
}
