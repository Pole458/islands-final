package it.unipr.sowide.gpj.tree;

import java.util.function.Function;

/**
 * A function which computes the result using only one parameter.
 *
 * @param <T> the type of the result of the evaluation of the tree
 * @param <C> the type of the "context" in which the evaluation of the tree is
 *            done; it can be used as a way to create a mutable state of
 *            computation as well as a way to pass input values.
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class UnaryFunction<T, C> implements Function1<T, C> {
    private final String name;
    private final Function<T, T> function;

    public UnaryFunction(String name, Function<T, T> function) {
        this.name = name;
        this.function = function;
    }

    @Override
    public UnaryFunction<T, C> transformInit() {
        // override if needed
        return this;
    }

    @Override
    public T apply(T input, C context) {
        return function.apply(input);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Operation<T, C> copy() {
        return new UnaryFunction<>(name, function);
    }

}
