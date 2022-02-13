package it.unipr.sowide.actodata.core.dataset.content.manycrud;

import it.unipr.sowide.actodes.interaction.Response;

public class NotFoundMany<K> implements Response {
    private final Iterable<K> keys;

    public NotFoundMany(Iterable<K> keys) {
        this.keys = keys;
    }

    public Iterable<K> getKeys() {
        return keys;
    }
}
