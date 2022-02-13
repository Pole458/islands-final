package it.unipr.sowide.actodata.core.engine.hyperparameters.content;

import it.unipr.sowide.actodes.interaction.Request;

import java.util.Map;

public class SetHyperParameters<K, H> implements Request {
    public final Map<K, H> hyperParameters;

    public SetHyperParameters(Map<K, H> hyperParameters) {
        this.hyperParameters = hyperParameters;
    }

    public Map<K, H> getAssignments() {
        return hyperParameters;
    }
}
