package it.unipr.sowide.gpj.tree;

import java.util.function.Supplier;

/**
 * A constant whose value is determined the first time it is evaluated.
 */
public class LateInitConstant<T, C> implements Terminal<T, C> {

    private final Supplier<T> initializer;

    /**
     * @param initializer the method use to initialize the value
     */
    public LateInitConstant(Supplier<T> initializer) {
        this.initializer = initializer;
    }


    @Override
    public T eval(C context) {
        throw new IllegalStateException(
                "InitializedConstant evaluated before initialization");
    }

    @Override
    public String getName() {
        return "LateInit(...)";
    }

    @Override
    public Constant<T, C> transformInit() {
        return new Constant<>(initializer.get());
    }

    @Override
    public LateInitConstant<T, C> copy() {
        return new LateInitConstant<>(initializer);
    }
}
