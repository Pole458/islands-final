package it.unipr.sowide.util;

import it.unipr.sowide.util.annotations.Namespace;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A set of utility static methods for {@link Set}s.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
@Namespace
public class Sets {
    private Sets() {
    }// do not instantiate

    /**
     * Returns the union of two sets.
     *
     * @param s1  the first set
     * @param s2  the second set
     * @param <E> the type of the elements of the two sets
     * @return the union
     */
    public static <E> Set<E> union(Set<? extends E> s1, Set<? extends E> s2) {
        return Stream.concat(s1.stream(), s2.stream())
                .collect(Collectors.toSet());
    }

    /**
     * Returns the union of several sets (at least two).
     *
     * @param s1  the first set
     * @param s2  the second set
     * @param ss  the other sets
     * @param <E> the type of the elements of the two sets
     * @return the union
     */
    @SafeVarargs
    public static <E> Set<E> union(
            Set<? extends E> s1,
            Set<? extends E> s2,
            Set<? extends E>... ss
    ) {

        Set<E> result = union(s1, s2);
        for (Set<? extends E> s : ss) {
            result = union(result, s);
        }
        return result;
    }

    /**
     * Returns the union between a set an a singleton.
     *
     * @param s       the set
     * @param element the element that composes the singleton
     * @param <E>     the type of the elements
     * @return the union
     */
    public static <E> Set<E> union(Set<? extends E> s, E element) {
        return union(s, Set.of(element));
    }
}
