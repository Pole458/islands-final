package it.unipr.sowide.gpj.tree;

import java.util.function.Function;

/**
 * A terminal value which returns a value which depends on the context.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class ContextDependentTerminal<T, C> implements Terminal<T, C> {
    private final String name;
    private final Function<C, T> evaluate;

    /**
     * @param name     the name of the operation
     * @param evaluate a function that takes the context in input
     */
    public ContextDependentTerminal(String name, Function<C, T> evaluate) {
        this.name = name;
        this.evaluate = evaluate;
    }

    @Override
    public T eval(C context) {
        return evaluate.apply(context);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ContextDependentTerminal<T, C> transformInit() {
        return this;
    }

    @Override
    public ContextDependentTerminal<T, C> copy() {
        return new ContextDependentTerminal<>(name, evaluate);
    }
}
