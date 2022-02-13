package it.unipr.sowide.actodata.core.controller.extensions;

import it.unipr.sowide.actodata.core.dataflow.Data;
import it.unipr.sowide.actodata.core.actodesext.promise.ActoPromise;
import it.unipr.sowide.actodata.core.engine.trainable.TrainableEngineComponent;
import it.unipr.sowide.actodata.core.engine.trainable.content.StartTraining;
import it.unipr.sowide.actodata.core.engine.trainable.content.StopTraining;
import it.unipr.sowide.actodata.core.engine.trainable.content.TrainingOutcome;
import it.unipr.sowide.actodes.registry.Reference;

import static it.unipr.sowide.actodata.core.actodesext.promise.ActoPromise.actoPromise;

/**
 * Controller extension used to control engines that use
 * {@link TrainableEngineComponent}s.
 *
 * @param <TI> the input data type for used the training
 * @param <TP> the type of the parameters used to control the training
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public interface TrainController<TI, TP> extends ControllerExtension {


    /**
     * Asks the engine if it is currently busy training
     *
     * @param engine the engine reference
     * @return a promise that resolves to true if the engine is training
     */
    default ActoPromise<Boolean> isTraining(Reference engine) {
        return actoPromise(getController()
                .queryProperty(engine, "isTraining", Boolean.class));
    }

    /**
     * Asks the engine if it is currently ready to start a training
     *
     * @param engine the engine reference
     * @return a promise that resolves to true if the engine is ready
     */
    default ActoPromise<Boolean> isReadyForTraining(Reference engine) {
        return getController()
                .queryProperty(engine, "isReadyForTraining", Boolean.class);
    }


    /**
     * Requests the engine to start a training task with the specified input
     * and parameters.
     *
     * @param trainingInput      the input
     * @param trainingParameters the parameters
     * @param engineReference    the reference to the engine
     * @return a promise that resolves to a {@link TrainingOutcome}.
     */
    default ActoPromise<TrainingOutcome> startTraining(
            TI trainingInput,
            TP trainingParameters,
            Reference engineReference
    ) {
        return getController().promiseFuture(
                engineReference,
                new StartTraining<>(
                        getController().wrapData(trainingInput),
                        trainingParameters
                )
        );
    }

    /**
     * Requests the engine to start a training task with the specified input
     * and parameters.
     *
     * @param trainingInput      the input behind a {@link Data} instance
     * @param trainingParameters the parameters
     * @param engineReference    the reference to the engine
     * @return a promise that resolves to a {@link TrainingOutcome}.
     */
    default ActoPromise<TrainingOutcome> startTraining(
            Data<TI> trainingInput,
            TP trainingParameters,
            Reference engineReference
    ) {
        return getController().promiseFuture(
                engineReference,
                new StartTraining<>(trainingInput, trainingParameters)
        );
    }

    /**
     * Requests the engine to stop any training task currently being carried.
     *
     * @param engineReference the reference to the engine
     * @param force           wheter if the engine should abruptly interrupt the
     *                        training task
     */
    default void stopTraining(Reference engineReference, boolean force) {
        getController().send(engineReference, new StopTraining(force));
    }
}
