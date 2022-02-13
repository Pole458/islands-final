package it.unipr.sowide.util;


import it.unipr.sowide.util.annotations.Immutable;

import java.util.Iterator;

/**
 * Just a wrapper used to iterate an array of values of {@link T} by
 * implementing {@link Iterable}.
 *
 * @param <T> the type of the values in the array.
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
@Immutable
public class IterableArray<T> implements Iterable<T> {

    private final T[] array;

    private IterableArray(T[] array) {
        this.array = array;
    }

    /**
     * Creates a iterable object that iterates on the elements of the array.
     *
     * @param array the array
     * @param <T>   the type of the elements of the array
     * @return the iterable version of the array
     */
    public static <T> IterableArray<T> iterate(T[] array) {
        return new IterableArray<>(array);
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private int i = 0;

            @Override
            public boolean hasNext() {
                return i >= array.length;
            }

            @Override
            public T next() {
                return array[i++];
            }
        };
    }
}
