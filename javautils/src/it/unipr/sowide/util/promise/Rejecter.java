package it.unipr.sowide.util.promise;

import it.unipr.sowide.util.Require;
import it.unipr.sowide.util.annotations.Immutable;

import java.util.function.Consumer;

/**
 * A special kind of {@link Consumer} used to accept rejection error values in
 * the initalization of a {@link Promise}.
 * @param <E> the type of the rejection error value.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
@Immutable
public interface Rejecter<E> extends Consumer<E> {
    @Override
    default void accept(E err) {
        reject(err);
    }

    @Override
    default Rejecter<E> andThen(Consumer<? super E> after) {
        Require.nonNull(after);
        return (E t) -> {
            accept(t);
            after.accept(t);
        };
    }

    /**
     * Rejects the promise being defined, using {@code err} as error value.
     * @param err the value representing the error
     */
    void reject(E err);
}
