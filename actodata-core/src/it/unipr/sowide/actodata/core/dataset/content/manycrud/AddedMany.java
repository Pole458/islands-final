package it.unipr.sowide.actodata.core.dataset.content.manycrud;

import it.unipr.sowide.actodes.interaction.Response;

public class AddedMany<K> implements Response {
    private final Iterable<K> added;

    public AddedMany(Iterable<K> added) {
        this.added = added;
    }

    public Iterable<K> getAdded() {
        return added;
    }
}
