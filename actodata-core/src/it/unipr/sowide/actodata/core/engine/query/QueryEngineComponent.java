package it.unipr.sowide.actodata.core.engine.query;

import it.unipr.sowide.actodata.core.dataflow.Data;
import it.unipr.sowide.actodata.core.actodesext.ActoDataBaseBehavior;
import it.unipr.sowide.actodata.core.actodesext.ActoDataCaseFactory;
import it.unipr.sowide.actodata.core.controller.extensions.ControllerExtension;
import it.unipr.sowide.actodata.core.controller.extensions.QueryController;
import it.unipr.sowide.actodata.core.engine.EngineComponent;
import it.unipr.sowide.actodata.core.engine.query.content.EngineQuery;
import it.unipr.sowide.util.CheckedCast;
import it.unipr.sowide.actodes.interaction.Error;
import it.unipr.sowide.actodata.core.actodesext.promise.ActoPromise;
import it.unipr.sowide.util.annotations.Open;
import it.unipr.sowide.util.promise.Rejecter;
import it.unipr.sowide.util.promise.Resolver;

import java.util.Set;

/**
 * A component of an engine that can be queried with an input to receive an
 * output (e.g. a classifier receives an untagged instance and returns the class
 * of the instance).
 *
 * @param <I> the type of the input values
 * @param <O> the type of the output values
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public abstract class QueryEngineComponent<I, O> implements EngineComponent {
    /**
     * {@inheritDoc}
     **/
    @Override
    public Set<Class<? extends ControllerExtension>>
    getControllerRequirements() {
        return Set.of(QueryController.class);
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public void defineFragmentCases(
            ActoDataBaseBehavior self,
            ActoDataCaseFactory c
    ) {
        c.onContentOfType(EngineQuery.class, (engineQuery, message) -> {
            new ActoPromise<O>((resolver, rejecter) -> {
                CheckedCast.cast((Data<I>) null, engineQuery.getQueryInput())
                        .ifSuccessfulOrElse(
                                queryInputCasted -> {
                                    handleQuery(
                                            queryInputCasted,
                                            self,
                                            resolver,
                                            rejecter
                                    );
                                }, (/*else*/) -> {
                                    rejecter.reject(Error.UNEXPECTEDCONTENT);
                                }
                        );
            }).then(result -> {
                self.send(message, result);
            }).onError(err -> {
                self.send(message, err);
            }).compel();
        });

        c.serveProperty("canBeQueried", this::canBeQueried);
    }

    /**
     * Resolves a query.
     *
     * @param input    the input value
     * @param resolver an acceptor for a result
     * @param rejecter an acceptor for errors
     */
    protected abstract void handleQuery(
            I input,
            Resolver<O> resolver,
            Rejecter<Error> rejecter
    );

    /**
     * Resolves a query.
     *
     * @param input the input value behind a {@link Data} instance
     * @param self the behavior of the actor that unwraps the data instance
     * @param resolver an acceptor for results
     * @param rejecter an acceptor for errors
     */
    @Open
    protected void handleQuery(
            Data<I> input,
            ActoDataBaseBehavior self,
            Resolver<O> resolver,
            Rejecter<Error> rejecter
    ) {
        input.getData(self).then(i -> {
            handleQuery(i, resolver, rejecter);
        }).compel();
    }

    /**
     * @return true if the engine can be queried in this moment.
     */
    protected abstract boolean canBeQueried();
}
