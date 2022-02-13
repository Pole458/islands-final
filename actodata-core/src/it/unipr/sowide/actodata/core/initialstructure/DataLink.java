package it.unipr.sowide.actodata.core.initialstructure;

/**
 * Represents the <i>declaration</i> link of a stream of data between an
 * ActoDatA node that acts as "producer" of data and an ActoDatA node that acts
 * as "consumer".
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
class DataLink {
    private final NodeDescriptor producer;
    private final NodeDescriptor consumer;

    public DataLink(NodeDescriptor producer, NodeDescriptor consumer) {
        this.producer = producer;
        this.consumer = consumer;
    }

    public NodeDescriptor getProducer() {
        return producer;
    }

    public NodeDescriptor getConsumer() {
        return consumer;
    }
}
