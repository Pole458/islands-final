package it.unipr.sowide.actodata.core.controller.extensions;

import it.unipr.sowide.actodata.core.actodesext.promise.ActoPromise;
import it.unipr.sowide.actodata.core.engine.Engine;
import it.unipr.sowide.actodata.core.engine.updatable.UpdatableEngineComponent;
import it.unipr.sowide.actodes.registry.Reference;

/**
 * A {@link ControllerExtension} used to control {@link Engine}s which include
 * an {@link UpdatableEngineComponent}.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 * @see BatchUpdateController
 * @see InstanceUpdateController
 */
public interface UpdateController extends ControllerExtension {
    /**
     * @param engine the engine reference
     * @return a promise that resolves to true if the engine is updating
     */
    default ActoPromise<Boolean> isUpdating(Reference engine) {
        return getController()
                .queryProperty(engine, "isUpdating", Boolean.class);
    }

    /**
     * @param engine the engine reference
     * @return a promise that resolves to true if the engine is ready to
     * initiate an update.
     */
    default ActoPromise<Boolean> isReadyForUpdate(Reference engine) {
        return getController()
                .queryProperty(engine, "isReadyForUpdate", Boolean.class);
    }
}
