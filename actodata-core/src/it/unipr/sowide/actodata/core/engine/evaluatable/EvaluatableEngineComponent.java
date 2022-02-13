package it.unipr.sowide.actodata.core.engine.evaluatable;

import it.unipr.sowide.actodata.core.dataflow.Data;
import it.unipr.sowide.actodata.core.actodesext.ActoDataBaseBehavior;
import it.unipr.sowide.actodata.core.actodesext.ActoDataCaseFactory;
import it.unipr.sowide.actodata.core.controller.extensions.ControllerExtension;
import it.unipr.sowide.actodata.core.controller.extensions.EvaluatableEngineController;
import it.unipr.sowide.actodata.core.engine.EngineComponent;
import it.unipr.sowide.actodata.core.engine.evaluatable.content.EvaluationOutcome;
import it.unipr.sowide.actodata.core.engine.evaluatable.content.EvaluationRequest;
import it.unipr.sowide.actodes.interaction.Error;
import it.unipr.sowide.util.annotations.Open;

import java.util.Set;
import java.util.function.Consumer;

import static it.unipr.sowide.util.CheckedCast.cast;

/**
 * Component of an engine that can be evaluated against a test set.
 *
 * @param <EI> the test set type
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 * @see EvaluatableEngineController
 */
public abstract class EvaluatableEngineComponent<EI>
        implements EngineComponent {

    /**
     * {@inheritDoc}
     **/
    @Override
    public Set<Class<? extends ControllerExtension>>
    getControllerRequirements() {
        return Set.of(EvaluatableEngineController.class);
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public void defineFragmentCases(
            ActoDataBaseBehavior self,
            ActoDataCaseFactory c
    ) {
        c.onContentOfType(EvaluationRequest.class, (evreq, message) -> {
            cast((Data<EI>) null, evreq.getTestSet())
                    .ifSuccessfulOrElse((ei -> {
                        this.evaluate(ei, self, (eo) -> {
                            self.send(message, eo);
                        });
                    }), (/*else*/) -> {
                        self.send(message, Error.UNEXPECTEDCONTENT);
                    });
        });
    }

    /**
     * Evaluates the engine against the {@code input} test set and returns an
     * {@link EvaluationOutcome} using the {@code outcomeAcceptor}.
     *
     * @param input           the input test set
     * @param outcomeAcceptor the outcome acceptor
     */
    public abstract void evaluate(
            EI input,
            Consumer<EvaluationOutcome> outcomeAcceptor
    );

    /**
     * Evaluates the engine against the {@code input} test set and returns an
     * {@link EvaluationOutcome} using the {@code outcomeAcceptor}.
     *
     * @param input           the input test set, behind a {@link Data} instance
     * @param outcomeAcceptor the outcome acceptor
     */
    @Open
    public void evaluate(
            Data<EI> input,
            ActoDataBaseBehavior self,
            Consumer<EvaluationOutcome> outcomeAcceptor
    ) {
        input.getData(self).then(
                ei -> this.evaluate(ei, outcomeAcceptor)
        ).compel();
    }
}
