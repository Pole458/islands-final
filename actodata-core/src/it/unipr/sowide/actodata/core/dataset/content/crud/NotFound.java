package it.unipr.sowide.actodata.core.dataset.content.crud;

import it.unipr.sowide.actodes.interaction.Response;

public class NotFound<K> implements Response {
    private final K key;

    public NotFound(K key) {
        this.key = key;
    }

    public K getKey() {
        return key;
    }
}
