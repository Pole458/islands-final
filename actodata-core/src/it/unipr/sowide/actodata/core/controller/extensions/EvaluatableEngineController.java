package it.unipr.sowide.actodata.core.controller.extensions;

import it.unipr.sowide.actodata.core.dataflow.Data;
import it.unipr.sowide.actodata.core.actodesext.promise.ActoPromise;
import it.unipr.sowide.actodata.core.engine.evaluatable.EvaluatableEngineComponent;
import it.unipr.sowide.actodata.core.engine.evaluatable.content.EvaluationOutcome;
import it.unipr.sowide.actodata.core.engine.evaluatable.content.EvaluationRequest;
import it.unipr.sowide.actodes.registry.Reference;

/**
 * Controller extension used to control engines that use
 * {@link EvaluatableEngineComponent}s. This allows controller to request
 * engines to evaluate themselves against a testSet and to provide the results.
 *
 * @param <TS> the type of the test set
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public interface EvaluatableEngineController<TS> extends ControllerExtension {
    /**
     * Requests the engine to evaluate itself against the specified testSet
     *
     * @param testSet the test set
     * @param engine  the reference of the engine
     * @return a promise that resolves into an {@link EvaluationOutcome}
     */
    default ActoPromise<EvaluationOutcome> evaluateEngine(
            TS testSet,
            Reference engine
    ) {
        return evaluateEngine(getController().wrapData(testSet), engine);
    }

    /**
     * Requests the engine to evaluate itself against the specified testSet
     *
     * @param testSet the test set behind a {@link Data} instance
     * @param engine  the reference of the engine
     * @return a promise that resolves into an {@link EvaluationOutcome}
     */
    default ActoPromise<EvaluationOutcome> evaluateEngine(
            Data<TS> testSet,
            Reference engine
    ) {
        return getController().promiseFuture(
                engine,
                new EvaluationRequest<>(testSet)
        );
    }
}
