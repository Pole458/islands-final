package it.unipr.sowide.actodata.core.engine.hyperparameters.content;

import it.unipr.sowide.actodes.interaction.Request;

import java.util.Set;

public class GetHyperParameters<K> implements Request {
    private final Set<K> parameterKeys;

    public GetHyperParameters(Set<K> parameterKeys) {
        this.parameterKeys = parameterKeys;
    }

    public Set<K> getParameterKeys() {
        return parameterKeys;
    }
}
