package it.unipr.sowide.gpj.tree;

/**
 * A terminal operation that simply returns always the same value.
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class Constant<T, C> implements Terminal<T, C> {
    private final T value;

    public Constant(T value){
        this.value = value;
    }

    @Override
    public Constant<T, C> transformInit() {
        // override if needed
        return this;
    }

    @Override
    public String getName() {
        return "«"+value.toString()+"»";
    }

    @Override
    public Operation<T, C> copy() {
        return new Constant<>(value);
    }

    public T eval(C context) {
        return value;
    }
}
