package it.unipr.sowide.actodata.core.dataset.content.manycrud;

import it.unipr.sowide.actodata.core.dataflow.DataTicket;
import it.unipr.sowide.actodes.interaction.Response;

public class ManyDataTicket<K, T> implements Response {
    private final Iterable<K> key;
    private final DataTicket<Iterable<T>, GetManyDataUnwrapped<K>> ticket;

    public ManyDataTicket(Iterable<K> key, DataTicket<Iterable<T>, GetManyDataUnwrapped<K>> ticket) {
        this.key = key;
        this.ticket = ticket;
    }

    public Iterable<K> getKey() {
        return key;
    }

    public DataTicket<Iterable<T>, GetManyDataUnwrapped<K>> getTicket() {
        return ticket;
    }


}
