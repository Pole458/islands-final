package it.unipr.sowide.util;

import it.unipr.sowide.util.annotations.Immutable;

import java.io.Serializable;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Represents a generic abstract data type of values composed by two main
 * subvalues of type {@link T1} and {@link T2}. It is immutable.
 *
 * @param <T1> the type of the first wrapped value
 * @param <T2> the type of the second wrapped value
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
@Immutable
public class Pair<T1, T2> implements Serializable {
    private final T1 _1;
    private final T2 _2;

    /**
     * Creates a new pair with {@code e1} and {@code e2} as wrapped values.
     *
     * @param e1 the first wrapped value.
     * @param e2 the second wrapped value.
     */
    public Pair(T1 e1, T2 e2) {
        this._1 = e1;
        this._2 = e2;
    }

    /**
     * @return the first wrapped value
     */
    public T1 get1() {
        return _1;
    }

    /**
     * @return the second wrapped value
     */
    public T2 get2() {
        return _2;
    }

    @Override
    public String toString() {
        return "Pair{«" + _1 + "», «" + _2 + "»}";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Pair) {
            return _1.equals(((Pair<?, ?>) obj)._1)
                    && _2.equals(((Pair<?, ?>) obj)._2);
        }
        return super.equals(obj);
    }

    /**
     * Transforms the first value by means of the application of
     * {@code function} on it and returns a new Pair with its result and the
     * original second wrapped value.
     *
     * @param function the function that transfomrs the first wrapped value
     * @param <R1>     the type of the result of the transformation
     * @return a new Pair with its result of {@code function} and the original
     * second wrapped value.
     */
    public <R1> Pair<R1, T2> map1(Function<T1, R1> function) {
        return Pair.of(function.apply(get1()), get2());
    }

    /**
     * Transforms the second value by means of the application of
     * {@code function} on it and returns a new Pair with the original first
     * wrapped value and its result.
     *
     * @param function the function that transfomrs the first wrapped value
     * @param <R2>     the type of the result of the transformation
     * @return a new Pair with the original first wrapped value and result of
     * {@code function}.
     */
    public <R2> Pair<T1, R2> map2(Function<T2, R2> function) {
        return Pair.of(get1(), function.apply(get2()));
    }

    /**
     * Applies the two values to {@code biFunction} and returns the result.
     *
     * @param biFunction the function to which the two wrapped values will be
     *                   applied to
     * @param <R>        the type of the result
     * @return the result of the application
     */
    public <R> R biApplyTo(BiFunction<T1, T2, R> biFunction) {
        return biFunction.apply(get1(), get2());
    }

    /**
     * Submits the two values to {@code biConsumer}.
     *
     * @param biConsumer the consumer to which the two values are submitted to.
     */
    public void biConsume(BiConsumer<T1, T2> biConsumer) {
        biConsumer.accept(get1(), get2());
    }

    /**
     * Creates a new pair with {@code e1} and {@code e2} as wrapped values.
     *
     * @param e1   the first wrapped value.
     * @param e2   the second wrapped value.
     * @param <T1> the type of the first value
     * @param <T2> the type of the second value
     * @return the new pair
     */
    public static <T1, T2>
    Pair<T1, T2> pair(T1 e1, T2 e2) {
        return new Pair<>(e1, e2);
    }

    /**
     * Creates a new pair with {@code e1} and {@code e2} as wrapped values.
     *
     * @param e1   the first wrapped value.
     * @param e2   the second wrapped value.
     * @param <T1> the type of the first value
     * @param <T2> the type of the second value
     * @return the new pair
     */
    public static <T1, T2>
    Pair<T1, T2> of(T1 e1, T2 e2) {
        return new Pair<>(e1, e2);
    }

    /**
     * Performs an action on both the values in the pair.
     *
     * @param pair   the pair with the values
     * @param action the action to be executed
     * @param <T>    the type of the values
     */
    public static <T> void doOnBoth(Pair<T, T> pair, Consumer<T> action) {
        action.accept(pair.get1());
        action.accept(pair.get2());
    }

    /**
     * Transforms both the values in the pair by means of the application of
     * the {@code function}. The function is executed two times, one for each
     * value, so be careful with unwanted side-effects.
     *
     * @param input    the pair to be transformed
     * @param function the function that defines the transformation
     * @param <T>      the input type
     * @param <R>      the output type
     * @return the pair after the two applications.
     */
    public static <T, R> Pair<R, R> map(
            Pair<T, T> input,
            Function<T, R> function
    ) {
        return input
                .map1(function)
                .map2(function);
    }


}
