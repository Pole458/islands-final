package it.unipr.sowide.actodata.core.engine.hyperparameters.content;

import it.unipr.sowide.actodes.interaction.Response;

import java.util.Map;

public class HyperParametersResponse<K, H> implements Response {
    private final Map<K, H> parametersValues;


    public HyperParametersResponse(Map<K, H> parametersValues) {
        this.parametersValues = parametersValues;
    }

    public Map<K, H> getParametersValues() {
        return parametersValues;
    }
}
