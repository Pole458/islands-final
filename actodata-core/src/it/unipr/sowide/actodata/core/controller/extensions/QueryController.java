package it.unipr.sowide.actodata.core.controller.extensions;

import it.unipr.sowide.actodata.core.dataflow.Data;
import it.unipr.sowide.actodata.core.actodesext.promise.ActoPromise;
import it.unipr.sowide.actodata.core.engine.query.QueryEngineComponent;
import it.unipr.sowide.actodata.core.engine.query.content.EngineQuery;
import it.unipr.sowide.actodes.registry.Reference;

/**
 * A controller used to query engines that use {@link QueryEngineComponent}s.
 * A <i>query</i> in this context simply represents the concept to send inputs
 * to the engine and to expect an output (e.g. a classifier receives an untagged
 * instance and returns the class of the instance).
 *
 * @param <I> the input type
 * @param <O> the output type
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public interface QueryController<I, O> extends ControllerExtension {
    /**
     * Queries the engine with the specified input
     *
     * @param input           the input
     * @param engineReference the reference to the engine
     * @return a promise that resolves to the output produced by the engine
     */
    default ActoPromise<O> queryEngine(I input, Reference engineReference) {
        return queryEngine(getController().wrapData(input), engineReference);
    }

    /**
     * Queries the engine with the specified input
     *
     * @param input  the input, behind a {@link Data} instance.
     * @param engine the reference to the engine
     * @return a promise that resolves to the output produced by the engine
     */
    default ActoPromise<O> queryEngine(Data<I> input, Reference engine) {
        return getController().promiseFuture(engine,
                new EngineQuery<>(input), (O) null
        );
    }

    /**
     * Asks the engine if it can be queried at the moment.
     *
     * @param engineReference the reference to the engine
     * @return a promise that resolves to true if the engine is ready to be
     * queried
     */
    default ActoPromise<Boolean> canBeQueried(Reference engineReference) {
        return getController()
                .queryProperty(engineReference, "canBeQueried", (Boolean) null);
    }
}
