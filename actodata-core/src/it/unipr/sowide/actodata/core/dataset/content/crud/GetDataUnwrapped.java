package it.unipr.sowide.actodata.core.dataset.content.crud;

import it.unipr.sowide.actodes.interaction.Request;

public class GetDataUnwrapped<K> implements Request {
    private final K key;

    public GetDataUnwrapped(K key) {
        this.key = key;
    }

    public K getKey() {
        return key;
    }
}
