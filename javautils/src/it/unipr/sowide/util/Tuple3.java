package it.unipr.sowide.util;

import it.unipr.sowide.util.annotations.Immutable;

/**
 * Like a {@link Pair}, but with three values.
 *
 * @param <T1> the type of the first value
 * @param <T2> the type of the second value
 * @param <T3> the type of the third value
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
@Immutable
public class Tuple3<T1, T2, T3> {
    private final T1 _1;
    private final T2 _2;
    private final T3 _3;

    /**
     * Creates a new Tuple3
     *
     * @param t1 the first value
     * @param t2 the second value
     * @param t3 the third value
     */
    public Tuple3(T1 t1, T2 t2, T3 t3) {
        _1 = t1;
        _2 = t2;
        _3 = t3;
    }

    /**
     * @return the first value
     */
    public T1 get_1() {
        return _1;
    }

    /**
     * @return the second value
     */
    public T2 get_2() {
        return _2;
    }

    /**
     * @return the third value
     */
    public T3 get_3() {
        return _3;
    }
}
