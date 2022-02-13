package it.unipr.sowide.actodata.core.engine.updatable;

import it.unipr.sowide.actodata.core.actodesext.ActoDataBaseBehavior;
import it.unipr.sowide.actodata.core.actodesext.ActoDataCaseFactory;
import it.unipr.sowide.actodata.core.controller.extensions.ControllerExtension;
import it.unipr.sowide.actodata.core.controller.extensions.UpdateController;
import it.unipr.sowide.actodata.core.engine.EngineComponent;

import java.util.Set;

/**
 * The component of an engine that can be updated.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 * @see BatchUpdatableEngineComponent
 * @see InstanceUpdatableEngineComponent
 */
public abstract class UpdatableEngineComponent implements EngineComponent {
    /**
     * {@inheritDoc}
     **/
    @Override
    public Set<Class<? extends ControllerExtension>>
    getControllerRequirements() {
        return Set.of(UpdateController.class);
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public void defineFragmentCases(
            ActoDataBaseBehavior self,
            ActoDataCaseFactory c
    ) {
        c.serveProperty("isUpdating", this::isUpdating);
        c.serveProperty("isReadyForUpdate", this::isReadyForUpdate);
    }

    /**
     * @return true if the engine is currently updating
     */
    public abstract boolean isUpdating();

    /**
     * @return true if the engine is currently ready for training
     */
    public abstract boolean isReadyForUpdate();
}
