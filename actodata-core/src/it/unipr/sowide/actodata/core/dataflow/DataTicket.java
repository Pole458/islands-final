package it.unipr.sowide.actodata.core.dataflow;

import it.unipr.sowide.actodata.core.actodesext.ActoDataBaseBehavior;
import it.unipr.sowide.actodata.core.actodesext.promise.ActoPromise;
import it.unipr.sowide.actodes.registry.Reference;

/**
 * A {@link DataTicket} is a particular kind of {@link Data} message that
 * delegates the retrieval of the actual data to another actor.
 *
 * @param <T> the type of the data
 * @param <R> the type of the message to be sent for the retrieval
 */
public class DataTicket<T, R> implements Data<T> {
    private final String typeName;
    private final Reference dataOwner;
    private final R requestMessage;


    /**
     * A data ticket for a data instance of type described by the string
     * {@code typeName}, owned by {@code dataOwner} and which requires the
     * sending of the {@code requestMessage} to the owner for the retrieval.
     *
     * @param typeName       the string that describes the type of the data
     * @param dataOwner      the actor to which the request message will be sent
     * @param requestMessage the request message to be sent at the moment of the
     *                       retrieval
     */
    public DataTicket(String typeName, Reference dataOwner, R requestMessage) {
        this.typeName = typeName;
        this.dataOwner = dataOwner;
        this.requestMessage = requestMessage;
    }

    /** {@inheritDoc} **/
    @Override
    public ActoPromise<T> getData(ActoDataBaseBehavior behavior) {
        return behavior.promiseFuture(dataOwner, requestMessage);
    }

    /**
     * @return the string that describes the type of the data
     */
    public String getTypeName() {
        return typeName;
    }

    @Override
    public String toString() {
        return "DataTicket{" +
                "typeName='" + typeName + '\'' +
                ", dataOwner=" + dataOwner +
                ", requestMessage=" + requestMessage +
                '}';
    }
}
