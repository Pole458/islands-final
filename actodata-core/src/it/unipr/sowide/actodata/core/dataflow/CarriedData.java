package it.unipr.sowide.actodata.core.dataflow;

import it.unipr.sowide.actodata.core.actodesext.ActoDataBaseBehavior;
import it.unipr.sowide.actodata.core.actodesext.promise.ActoPromise;
import it.unipr.sowide.actodata.core.actodesext.promise.ActoPromises;

/**
 * Implementation of {@link Data} that simply encapsulates the data inside
 * the message itself.
 *
 * @param <T> the type of the data
 */
public class CarriedData<T> implements Data<T> {
    private final T data;

    /**
     * A message that incapsulates the provided data.
     *
     * @param data the data instance
     */
    public CarriedData(T data) {
        this.data = data;
    }

    /**
     * {@inheritDoc}
     *
     * @implNote This implementation simply returns the data instance
     * encapsulated in the message.
     **/
    @Override
    public ActoPromise<T> getData(ActoDataBaseBehavior behavior) {
        return ActoPromises.immediatelyResolve(data);
    }

    @Override
    public String toString() {
        return "CarriedData{" +
                "data=" + data +
                '}';
    }
}
