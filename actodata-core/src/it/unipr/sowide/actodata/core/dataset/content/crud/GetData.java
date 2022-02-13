package it.unipr.sowide.actodata.core.dataset.content.crud;

import it.unipr.sowide.actodes.interaction.Request;

public class GetData<K> implements Request {
    private final K key;

    public GetData(K key) {
        this.key = key;
    }

    public K getKey() {
        return key;
    }
}
