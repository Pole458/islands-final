package it.unipr.sowide.actodata.core.controller.extensions;

import it.unipr.sowide.actodata.core.dataflow.Data;
import it.unipr.sowide.actodata.core.engine.updatable.InstanceUpdatableEngineComponent;
import it.unipr.sowide.actodata.core.engine.updatable.content.UpdateByInstance;
import it.unipr.sowide.actodes.registry.Reference;

/**
 * A controller extension used to control engines which use
 * {@link InstanceUpdatableEngineComponent}s.
 *
 * @param <I> the type of the instances
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public interface InstanceUpdateController<I> extends UpdateController{
    /**
     * Requests the engine to update itself using the provided instance.
     *
     * @param instance the data instance
     * @param engine the reference to the engine
     */
    default void updateByInstance(I instance, Reference engine){
        updateByInstance(getController().wrapData(instance), engine);
    }

    /**
     * Requests the engine to update itself using the provided instance.
     *
     * @param instance the data instance behind a {@link Data} object
     * @param engine the reference to the engine
     */
    default void updateByInstance(Data<I> instance, Reference engine){
        getController().send(engine, new UpdateByInstance<>(instance));
    }
}
