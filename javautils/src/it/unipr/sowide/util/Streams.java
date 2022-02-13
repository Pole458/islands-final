package it.unipr.sowide.util;

import it.unipr.sowide.util.annotations.Namespace;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A collection of static methods to build and to operate on {@link Stream}s.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
@Namespace
public class Streams {
    private Streams() {
    } //do not instantiate

    /**
     * Creates a stream from an iterable of elements.
     *
     * @param input the iterable from which the elements of the streams are
     *              taken from
     * @param <T>   the type of the elements
     * @return the stream
     */
    public static <T> Stream<T> stream(Iterable<T> input) {
        return StreamSupport.stream(input.spliterator(), false);
    }

    /**
     * Returns a function for {@link Stream#flatMap(Function)}, filters
     * all the optional values that are empty and provides a stream of the
     * wrapped values.
     *
     * @param <T> the type of the wrapped values of the optionals
     * @return the function to be used as argument of
     * {@link Stream#flatMap(Function)}.
     */
    public static <T> Function<Optional<T>, Stream<T>> filterEmptys() {
        return (opt) -> {
            if (opt.isEmpty()) {
                return Stream.empty();
            } else {
                return Stream.of(opt.get());
            }
        };
    }

    private static <T> Iterable<Pair<Integer, T>> iterateWithIndex(
            Iterable<T> input
    ) {
        return () -> {
            Iterator<T> iterator = input.iterator();
            return new Iterator<>() {
                private int i = 0;

                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public Pair<Integer, T> next() {
                    return Pair.of(i++, iterator.next());
                }
            };
        };
    }

    /**
     * Creates a new stream of pairs, where the first value is the index of
     * the element and the second value is the element in the iterable.
     *
     * @param input the iterable from which the stream is built
     * @param <T>   the type of the elements
     * @return the stream
     */
    public static <T> Stream<Pair<Integer, T>> streamWithIndex(
            Iterable<T> input
    ) {
        return StreamSupport.stream(
                iterateWithIndex(input).spliterator(),
                false
        );
    }

    /**
     * Returns a function for {@link Stream#flatMap(Function)}. Filters away
     * all the elements that are not instances of the specified class and
     * provides a stream of elements casted to the type represented by the
     * specified class.
     *
     * @param toClass the class
     * @param <T> the output stream
     * @return the function to be used as argument of
     * {@link Stream#flatMap(Function)}.
     */
    public static <T> Function<Object, Stream<? extends T>> filterCast(
            Class<? extends T> toClass
    ) {
        return (o -> {
            if (toClass.isInstance(o)) {
                return Stream.of(toClass.cast(o));
            } else {
                return Stream.empty();
            }
        });
    }

}
