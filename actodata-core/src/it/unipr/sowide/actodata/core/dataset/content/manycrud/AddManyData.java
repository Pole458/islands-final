package it.unipr.sowide.actodata.core.dataset.content.manycrud;

import it.unipr.sowide.actodes.interaction.Request;

public class AddManyData<T> implements Request {
    private final Iterable<T> manyData;

    public AddManyData(Iterable<T> manyData) {
        this.manyData = manyData;
    }

    public Iterable<T> getManyData() {
        return manyData;
    }
}
