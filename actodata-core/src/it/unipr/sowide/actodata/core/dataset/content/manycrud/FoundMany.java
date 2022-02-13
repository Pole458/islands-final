package it.unipr.sowide.actodata.core.dataset.content.manycrud;

import it.unipr.sowide.util.Pair;
import it.unipr.sowide.actodes.interaction.Response;

public class FoundMany<K, T> implements Response {
    private final Iterable<Pair<K, T>> results;

    public FoundMany(Iterable<Pair<K, T>> results) {
        this.results = results;
    }


    public Iterable<Pair<K, T>> getResults() {
        return results;
    }
}
