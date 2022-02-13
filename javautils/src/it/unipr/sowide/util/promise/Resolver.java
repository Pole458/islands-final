package it.unipr.sowide.util.promise;

import it.unipr.sowide.util.annotations.Immutable;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * A special kind of {@link Consumer} used to accept resolved values in the
 * initalization of a {@link Promise}.
 * @param <TT> the type of the resolved value.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
@Immutable
public interface Resolver<TT> extends Consumer<TT> {
    @Override
    default void accept(TT tt) {
        resolve(tt);
    }


    @Override
    default Resolver<TT> andThen(Consumer<? super TT> after) {
        Objects.requireNonNull(after);
        return (TT t) -> {
            accept(t);
            after.accept(t);
        };
    }

    /**
     * Resolves the promise being defined, using {@code tt} as result.
     * @param tt the result value.
     */
    void resolve(TT tt);
}
