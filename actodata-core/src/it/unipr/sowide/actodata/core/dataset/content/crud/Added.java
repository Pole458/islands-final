package it.unipr.sowide.actodata.core.dataset.content.crud;

import it.unipr.sowide.actodes.interaction.Response;

public class Added<K> implements Response {
    private final K atKey;

    public Added(K atKey) {
        this.atKey = atKey;
    }

    public K getAtKey() {
        return atKey;
    }
}
