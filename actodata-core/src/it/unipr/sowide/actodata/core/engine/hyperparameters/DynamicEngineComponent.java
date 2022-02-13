package it.unipr.sowide.actodata.core.engine.hyperparameters;

import it.unipr.sowide.actodata.core.actodesext.ActoDataBaseBehavior;
import it.unipr.sowide.actodata.core.actodesext.ActoDataCaseFactory;
import it.unipr.sowide.actodata.core.controller.extensions.ControllerExtension;
import it.unipr.sowide.actodata.core.controller.extensions.DynamicEngineController;
import it.unipr.sowide.actodata.core.engine.hyperparameters.content.SetHyperParameters;
import it.unipr.sowide.util.CheckedCast;
import it.unipr.sowide.util.Sets;
import it.unipr.sowide.actodes.interaction.Error;

import java.util.Map;
import java.util.Set;

/**
 * A component of an engine whose parameters can be changed by other actors.
 *
 * @param <K> the type of the keys used to distinguish the parameters
 * @param <H> the type of the values of the parameters
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public abstract class DynamicEngineComponent<K, H>
        extends OpenEngineComponent<K, H> {

    /**
     * {@inheritDoc}
     **/
    @Override
    public Set<Class<? extends ControllerExtension>>
    getControllerRequirements() {
        return Sets.union(
                super.getControllerRequirements(),
                DynamicEngineController.class
        );
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public void defineFragmentCases(
            ActoDataBaseBehavior self,
            ActoDataCaseFactory c
    ) {
        super.defineFragmentCases(self, c);

        c.onContentOfType(SetHyperParameters.class, (set, message) -> {
            CheckedCast.cast((Map<K, H>) null, set.getAssignments())
                    .ifSuccessfulOrElse(
                            this::setHyperParameters,
                            (/*else*/) -> {
                                self.send(message, Error.UNEXPECTEDCONTENT);
                            }
                    );
        });
    }

    /**
     * Sets the internal parameters, using the given assignments.
     *
     * @param assignments the assignments
     */
    public abstract void setHyperParameters(Map<K, H> assignments);
}
