package it.unipr.sowide.util;

import it.unipr.sowide.util.annotations.Immutable;

import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

/**
 * A Generator is an {@link Iterable} that returns values generated on the
 * spot when iterated.
 *
 * @param <T> the type of the values generated
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
@Immutable
public class Generator<T> implements Iterable<T> {

    private final Supplier<Optional<T>> supplier;

    /**
     * Creates a new generator.
     *
     * @param supplier method used to generate new values. At each invocation,
     *                 the supplier should return an {@link Optional#of(Object)}
     *                 or {@link Optional#empty()}; an empty optional is treated
     *                 as a signal that the generation of values is terminated.
     */
    public Generator(Supplier<Optional<T>> supplier) {
        this.supplier = supplier;
    }


    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private T cached = null;

            @Override
            public boolean hasNext() {
                Optional<T> t = supplier.get();
                if (t.isPresent()) {
                    cached = t.get();
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public T next() {
                return cached;
            }
        };
    }

    /**
     * Builds an infinite generator that generates long integer values, starting
     * from {@code 0L} and incrementing by 1 each time. It does not stop, so
     * be careful for wrap-around, if you get to {@link Long#MAX_VALUE}.
     *
     * @return an infinite generator that generates long integer values2
     */
    public static Generator<Long> positiveLongs() {
        var i = new AtomicLong(0L);
        return new Generator<>(() -> Optional.of(i.getAndIncrement()));
    }
}
