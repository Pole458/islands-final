package it.unipr.sowide.actodata.core.dataset.content.crud;

import it.unipr.sowide.actodes.interaction.Request;

public class AddData<T> implements Request {
    private final T data;

    public AddData(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }
}
