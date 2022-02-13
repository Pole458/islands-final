package it.unipr.sowide.gpj.tree;

import java.util.HashMap;

/**
 * A Terminal value that extracts a named value from the context which is
 * defined as an {@link HashMap}.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class NamedVariable<T> extends
        ContextDependentTerminal<T, HashMap<String, T>> {
    private final String varName;

    /**
     * @param varName the name of the variable
     */
    public NamedVariable(String varName) {
        super(varName, (map) -> map.get(varName));
        this.varName = varName;
    }

    public String getVarName() {
        return varName;
    }

    @Override
    public NamedVariable<T> transformInit() {
        return this;
    }

    @Override
    public NamedVariable<T> copy() {
        return new NamedVariable<>(varName);
    }
}
