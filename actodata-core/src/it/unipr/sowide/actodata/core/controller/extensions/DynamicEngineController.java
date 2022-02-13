package it.unipr.sowide.actodata.core.controller.extensions;

import it.unipr.sowide.actodata.core.engine.hyperparameters.DynamicEngineComponent;
import it.unipr.sowide.actodata.core.engine.hyperparameters.content.SetHyperParameters;
import it.unipr.sowide.actodes.registry.Reference;

import java.util.Map;

/**
 * Controller extension used to control engines that include a
 * {@link DynamicEngineComponent}. A dynamic engine is an open engine that
 * allows external actors to change all, or some, of its parameters.
 *
 * @param <K> the key type used to distinguish the parameters
 * @param <H> the value type of the parameters
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 * @see OpenEngineController
 */
public interface DynamicEngineController<K, H>
        extends OpenEngineController<K, H> {
    /**
     * Requests the engine to set the parameters according to the assignments
     * in the provided {@code assignments} map.
     *
     * @param assignments     the assignments
     * @param engineReference the engine reference
     */
    default void setHyperparameters(
            Map<K, H> assignments,
            Reference engineReference
    ) {
        getController().send(
                engineReference,
                new SetHyperParameters<>(assignments)
        );
    }
}
