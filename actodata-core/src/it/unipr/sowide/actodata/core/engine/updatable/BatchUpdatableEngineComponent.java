package it.unipr.sowide.actodata.core.engine.updatable;

import it.unipr.sowide.actodata.core.dataflow.Data;
import it.unipr.sowide.actodata.core.actodesext.ActoDataBaseBehavior;
import it.unipr.sowide.actodata.core.actodesext.ActoDataCaseFactory;
import it.unipr.sowide.actodata.core.controller.extensions.BatchUpdateController;
import it.unipr.sowide.actodata.core.controller.extensions.ControllerExtension;
import it.unipr.sowide.actodata.core.engine.updatable.content.UpdateByBatch;
import it.unipr.sowide.util.CheckedCast;
import it.unipr.sowide.util.Sets;
import it.unipr.sowide.actodes.interaction.Done;
import it.unipr.sowide.actodes.interaction.Error;
import it.unipr.sowide.util.annotations.Open;

import java.util.Set;

/**
 * The component of an engine that can be update with batches of data.
 *
 * @param <B> the type of the data batches
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public abstract class BatchUpdatableEngineComponent<B>
        extends UpdatableEngineComponent {
    /**
     * {@inheritDoc}
     **/
    @Override
    public Set<Class<? extends ControllerExtension>>
    getControllerRequirements() {
        return Sets.union(
                super.getControllerRequirements(),
                BatchUpdateController.class
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

        c.onContentOfType(UpdateByBatch.class, (updateByBatch, message) -> {
            CheckedCast.cast((Data<B>) null, updateByBatch.getBatch())
                    .ifSuccessfulOrElse((batch) -> {
                        updateByBatch(batch, self);
                        self.send(message, Done.DONE);
                    }, (/*else*/) -> {
                        self.send(message, Error.UNEXPECTEDCONTENT);
                    });
        });
    }

    /**
     * Updates the engine using the provided batch of data
     *
     * @param batch the batch of data
     */
    public abstract void updateByBatch(B batch);

    /**
     * Updates the engine using the provided batch of data
     *
     * @param batch the batch of data behind a {@link Data} instance
     * @param self the behavior of the engine actor
     */
    @Open
    public void updateByBatch(Data<B> batch, ActoDataBaseBehavior self) {
        batch.getData(self).then(this::updateByBatch).compel();
    }
}
