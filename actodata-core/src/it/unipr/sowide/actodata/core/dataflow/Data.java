package it.unipr.sowide.actodata.core.dataflow;

import it.unipr.sowide.actodata.core.actodesext.ActoDataBaseBehavior;
import it.unipr.sowide.actodata.core.actodesext.promise.ActoPromise;

import java.io.Serializable;

/**
 * Message that represent the existence of a piece of data of type {@link T} and
 * the operations needed to retrieve it.
 *
 * @param <T> the type of the data
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public interface Data<T> extends Serializable {
    /**
     * The method used to retrieve the actual instance of {@link T} that reifies
     * the data.
     *
     * @param behavior the behavior of the actor requesting the
     *                 <i>unwrapping</i> of the data.
     * @return the actual instance of {@link T}.
     */
    ActoPromise<T> getData(ActoDataBaseBehavior behavior);
}
