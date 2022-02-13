package it.unipr.sowide.actodata.core.engine.updatable;

import it.unipr.sowide.actodata.core.dataflow.Data;
import it.unipr.sowide.actodata.core.actodesext.ActoDataBaseBehavior;
import it.unipr.sowide.actodata.core.actodesext.ActoDataCaseFactory;
import it.unipr.sowide.actodata.core.controller.extensions.ControllerExtension;
import it.unipr.sowide.actodata.core.controller.extensions.InstanceUpdateController;
import it.unipr.sowide.actodata.core.engine.updatable.content.UpdateByInstance;
import it.unipr.sowide.util.CheckedCast;
import it.unipr.sowide.util.Sets;
import it.unipr.sowide.actodes.interaction.Done;
import it.unipr.sowide.actodes.interaction.Error;
import it.unipr.sowide.util.annotations.Open;

import java.util.Set;

/**
 * Component of an engine that can be update by instances of data
 *
 * @param <I> the type of the instances
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public abstract class InstanceUpdatableEngineComponent<I>
        extends UpdatableEngineComponent {

    /**
     * {@inheritDoc}
     **/
    @Override
    public Set<Class<? extends ControllerExtension>>
    getControllerRequirements() {
        return Sets.union(super.getControllerRequirements(), InstanceUpdateController.class);
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

        c.onContentOfType(UpdateByInstance.class, (update, message) -> {
            CheckedCast.cast((Data<I>) null, update.getInstance())
                    .ifSuccessfulOrElse((instance) -> {
                        updateByInstance(instance, self);
                        self.send(message, Done.DONE);
                    }, (/*else*/) -> {
                        self.send(message, Error.UNEXPECTEDCONTENT);
                    });
        });
    }

    /**
     * Updates the engine using the provided instance of data
     *
     * @param instance the instance of data
     */
    public abstract void updateByInstance(I instance);

    /**
     * Updates the engine using the provided instance of data
     *
     * @param instance the instance of data behind a {@link Data} instance
     * @param self     the behavior of the engine actor
     */
    @Open
    public void updateByInstance(
            Data<I> instance,
            ActoDataBaseBehavior self
    ) {
        instance.getData(self).then(this::updateByInstance).compel();
    }
}
