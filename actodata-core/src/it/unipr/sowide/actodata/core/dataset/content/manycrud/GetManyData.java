package it.unipr.sowide.actodata.core.dataset.content.manycrud;

import it.unipr.sowide.actodes.interaction.Request;

public class GetManyData<K> implements Request {
    private final Iterable<K> keys;

    public GetManyData(Iterable<K> keys) {
        this.keys = keys;
    }

    public Iterable<K> getKeys() {
        return keys;
    }
}
