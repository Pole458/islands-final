package it.unipr.sowide.actodata.core.dataset.content.crud;

import it.unipr.sowide.actodata.core.dataflow.DataTicket;
import it.unipr.sowide.actodes.interaction.Response;

public class FoundDataTicket<K, V> implements Response {
    private final K key;
    private final DataTicket<V, GetDataUnwrapped<K>> ticket;

    public FoundDataTicket(K key, DataTicket<V, GetDataUnwrapped<K>> ticket) {
        this.key = key;
        this.ticket = ticket;
    }

    public K getKey() {
        return key;
    }

    public DataTicket<V, GetDataUnwrapped<K>> getTicket() {
        return ticket;
    }
}
