package it.unipr.sowide.actodata.core.dataset.content.crud;

import it.unipr.sowide.actodes.interaction.Request;

public class PutData<K, T> implements Request {
    private final K key;
    private final T data;

    public PutData(K key, T data) {
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
