package it.unipr.sowide.actodata.core.controller.extensions;

import it.unipr.sowide.actodata.core.actodesext.promise.ActoPromise;
import it.unipr.sowide.actodata.core.engine.hyperparameters.OpenEngineComponent;
import it.unipr.sowide.actodata.core.engine.hyperparameters.content.GetHyperParameters;
import it.unipr.sowide.actodata.core.engine.hyperparameters.content.HyperParametersResponse;
import it.unipr.sowide.actodes.registry.Reference;

import java.util.Map;
import java.util.Set;

import static it.unipr.sowide.actodata.core.actodesext.promise.ActoPromise.actoPromise;

/**
 * A controller extension with methods used to control engine which use
 * {@link OpenEngineComponent}s. An "open" engine is an engine which all or part
 * of the parameters are accessible for retrieval with simple requests by the
 * other actors.
 *
 * @param <K> the key type, used to distinguish the parameters
 * @param <H> the type possible values of the parameters
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public interface OpenEngineController<K, H> extends ControllerExtension {

    /**
     * Requests the engine to provide the values of the specified parameters.
     *
     * @param keys            the set of the keys identifying the parameters
     * @param engineReference the reference of the engine
     * @return a promise that resolves into a {@link Map} where each entry
     * corresponds to a pair [requested parameter, found value].
     */
    default ActoPromise<Map<K, H>> getParameters(
            Set<K> keys,
            Reference engineReference
    ) {
        return actoPromise(getController().promiseFuture(
                engineReference,
                new GetHyperParameters<>(keys),
                (HyperParametersResponse<K, H>) null
        ).map(HyperParametersResponse::getParametersValues));
    }


}
