package it.unipr.sowide.gpj.tree;

import java.util.function.BiFunction;

/**
 * A function with arity of two.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class BinaryFunction<T, C> implements Function2<T, C> {
    private final String name;
    private final BiFunction<T, T, T> function;

    public BinaryFunction(String name, BiFunction<T, T, T> function) {
        this.name = name;
        this.function = function;
    }

    @Override
    public BinaryFunction<T, C> transformInit() {
        // override if needed
        return this;
    }

    @Override
    public T apply(T t1, T t2, C context) {
        return function.apply(t1, t2);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Operation<T, C> copy() {
        return new BinaryFunction<>(name, function);
    }
}
