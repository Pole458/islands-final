package it.unipr.sowide.actodata.core.dataset.content.crud;

import it.unipr.sowide.actodes.interaction.Response;

public class Found<K, T> implements Response {
    private final K key;
    private final T data;

    public Found(K key, T data) {
        this.key = key;
        this.data = data;
    }

    public K getKey() {
        return key;
    }

    public T getData() {
        return data;
    }
}
