package it.unipr.sowide.actodata.core.engine.hyperparameters;

import it.unipr.sowide.actodata.core.actodesext.ActoDataBaseBehavior;
import it.unipr.sowide.actodata.core.actodesext.ActoDataCaseFactory;
import it.unipr.sowide.actodata.core.controller.extensions.ControllerExtension;
import it.unipr.sowide.actodata.core.controller.extensions.OpenEngineController;
import it.unipr.sowide.actodata.core.engine.EngineComponent;
import it.unipr.sowide.actodata.core.engine.hyperparameters.content.GetHyperParameters;
import it.unipr.sowide.actodata.core.engine.hyperparameters.content.HyperParametersResponse;
import it.unipr.sowide.util.CheckedCast;
import it.unipr.sowide.actodes.interaction.Error;

import java.util.Map;
import java.util.Set;

/**
 * A component of an engine whose parameters can be retrieved by other actors.
 *
 * @param <K> the type of the keys used to distinguish the parameters
 * @param <H> the type of the values of the parameters
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public abstract class OpenEngineComponent<K, H> implements EngineComponent {
    /**
     * {@inheritDoc}
     **/
    @Override
    public Set<Class<? extends ControllerExtension>>
    getControllerRequirements() {
        return Set.of(OpenEngineController.class);
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public void defineFragmentCases(
            ActoDataBaseBehavior self,
            ActoDataCaseFactory c
    ) {
        c.onContentOfType(GetHyperParameters.class, (get, message) -> {
            CheckedCast.cast((Set<K>) null, get.getParameterKeys())
                    .ifSuccessfulOrElse((parametersKeys) -> {
                        self.send(message, new HyperParametersResponse<>(
                                getHyperParameters(parametersKeys)
                        ));
                    }, (/*else*/) -> {
                        self.send(message, Error.UNEXPECTEDCONTENT);
                    });
        });
    }

    /**
     * Returns the parameters of the engine, according to the requested
     * {@code keys}.
     * @param keys the keys
     * @return a map containing key-parameter values entries for the requested
     * parameters
     */
    public abstract Map<K, H> getHyperParameters(Set<K> keys);

}
