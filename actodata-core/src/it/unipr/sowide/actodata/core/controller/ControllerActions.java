package it.unipr.sowide.actodata.core.controller;

import it.unipr.sowide.actodata.core.actodesext.ActoDataBehaviorActions;
import it.unipr.sowide.actodata.core.dataflow.ReferenceSet;
import it.unipr.sowide.actodata.core.engine.Engine;

/**
 * Methods that represents internal actions that can be carried by a
 * {@link Controller}.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public interface ControllerActions extends ActoDataBehaviorActions {
    /**
     * Sends the provided data to the subscribed actors.
     *
     * @param data the data
     */
    void sendOutput(Object data);

    /**
     * Returns the set of controlled {@link Engine}s.
     *
     * @return the controlled engines.
     */
    ReferenceSet getControlledEngines();
}
