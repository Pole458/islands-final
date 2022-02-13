package it.unipr.sowide.actodata.core.controller.extensions;

import it.unipr.sowide.actodata.core.dataflow.Data;
import it.unipr.sowide.actodata.core.actodesext.promise.ActoPromise;
import it.unipr.sowide.actodata.core.engine.updatable.BatchUpdatableEngineComponent;
import it.unipr.sowide.actodata.core.engine.updatable.content.UpdateByBatch;
import it.unipr.sowide.actodes.interaction.Done;
import it.unipr.sowide.actodes.registry.Reference;

/**
 * A controller extension used to control engines which use
 * {@link BatchUpdatableEngineComponent}s.
 *
 * @param <I> the type of the batch
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public interface BatchUpdateController<I> extends UpdateController {
    /**
     * Requests an engine to update itself using the provided batch of data
     *
     * @param batch  the batch
     * @param engine the engine reference
     * @return a promise that resolves to {@link Done#DONE} when done.
     */
    default ActoPromise<Done> updateByBatch(I batch, Reference engine) {
        return updateByBatch(getController().wrapData(batch), engine);
    }

    /**
     * Requests an engine to update itself using the provided batch of data
     * wrapped in a {@link Data} instance.
     *
     * @param batch  the batch
     * @param engine the engine reference
     * @return a promise that resolves to {@link Done#DONE} when done.
     */
    default ActoPromise<Done> updateByBatch(Data<I> batch, Reference engine) {
        return getController().promiseFuture(
                engine,
                new UpdateByBatch<>(batch),
                (Done) null
        );
    }
}
