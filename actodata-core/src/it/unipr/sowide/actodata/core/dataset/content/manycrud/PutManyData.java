package it.unipr.sowide.actodata.core.dataset.content.manycrud;

import it.unipr.sowide.util.Pair;

public class PutManyData<K, T> {
    private final Iterable<Pair<K, T>> dataPairs;

    public PutManyData(Iterable<Pair<K, T>> dataPairs) {
        this.dataPairs = dataPairs;
    }

    public Iterable<Pair<K, T>> getDataPairs() {
        return dataPairs;
    }
}
