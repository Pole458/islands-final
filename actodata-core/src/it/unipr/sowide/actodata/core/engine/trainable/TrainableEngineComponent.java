package it.unipr.sowide.actodata.core.engine.trainable;

import it.unipr.sowide.actodata.core.dataflow.Data;
import it.unipr.sowide.actodata.core.actodesext.ActoDataBaseBehavior;
import it.unipr.sowide.actodata.core.actodesext.ActoDataCaseFactory;
import it.unipr.sowide.actodata.core.controller.extensions.ControllerExtension;
import it.unipr.sowide.actodata.core.controller.extensions.TrainController;
import it.unipr.sowide.actodata.core.engine.EngineComponent;
import it.unipr.sowide.actodata.core.engine.trainable.content.StartTraining;
import it.unipr.sowide.actodata.core.engine.trainable.content.StopTraining;
import it.unipr.sowide.actodata.core.engine.trainable.content.TrainingError;
import it.unipr.sowide.actodata.core.engine.trainable.content.TrainingOutcome;
import it.unipr.sowide.actodes.interaction.Done;
import it.unipr.sowide.actodes.interaction.Error;
import it.unipr.sowide.util.annotations.Open;

import java.util.Set;
import java.util.function.Consumer;

/**
 * The component of an engine that can be trained.
 *
 * @param <TI> training input data type
 * @param <TP> training parameters data type
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public abstract class TrainableEngineComponent<TI, TP>
        implements EngineComponent {
    /**
     * {@inheritDoc}
     **/
    @Override
    public Set<Class<? extends ControllerExtension>>
    getControllerRequirements() {
        return Set.of(TrainController.class);
    }

    private final Class<? extends StartTraining<TI, TP>> startTrainingClass;
    private final Class<? extends StopTraining> stopTrainingClass;

    /**
     * Creates a {@link TrainableEngineComponent} that accepts messages of the
     * specified types to request the start and the sto of the trainings.
     *
     * @param startTrainingClass the type of the start training message
     * @param stopTrainingClass  the type of the stop training message
     */
    public TrainableEngineComponent(
            Class<? extends StartTraining<TI, TP>> startTrainingClass,
            Class<? extends StopTraining> stopTrainingClass
    ) {
        this.startTrainingClass = startTrainingClass;
        this.stopTrainingClass = stopTrainingClass;
    }

    /**
     * Creates a {@link TrainableEngineComponent} that accepts messages of types
     * {@link StartTraining} and {@link StopTraining}.
     */
    @SuppressWarnings({"unchecked", "RedundantCast"})
    public TrainableEngineComponent() {
        this.startTrainingClass =
                (Class<? extends StartTraining<TI, TP>>)
                        (Class<?>) StartTraining.class;
        this.stopTrainingClass = StopTraining.class;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public void defineFragmentCases(
            ActoDataBaseBehavior self,
            ActoDataCaseFactory c
    ) {

        c.onContentOfType(startTrainingClass, (startTraining, message) -> {
            startTraining(
                    startTraining.getInput(),
                    startTraining.getTrainingParameters(),
                    self,
                    (trainingOutcome) -> self.send(message, trainingOutcome)
            );
        });
        c.onContentOfType(stopTrainingClass, (stopTraining, message) -> {
            if (canTrainingBeStopped()) {
                try {
                    stopTraining(false);
                    self.send(message, Done.DONE);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    self.send(message, Error.FAILEDEXECUTION);
                }
            } else if (stopTraining.mustForce()) {
                try {
                    stopTraining(true);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                self.send(message, Done.DONE);
            } else {
                self.send(message, Error.REFUSEDREQUEST);
            }
        });
        c.serveProperty("isTraining", this::isTraining);
        c.serveProperty("isReadyForTraining", this::isReadyForTraining);
    }

    /**
     * Starts the training task.
     *
     * @param inputData       the input data used to train the engine
     * @param parameters      the parameters of the training task
     * @param outcomeAcceptor an acceptor that accepts the result outcome
     */
    public abstract void startTraining(
            TI inputData,
            TP parameters,
            Consumer<TrainingOutcome> outcomeAcceptor
    );

    /**
     * Starts the training task.
     *
     * @param inputData       the input data used to train the engine, behind a
     *                        {@link Data} instance
     * @param parameters      the parameters of the training task
     * @param context         the behavior of the engine actor
     * @param outcomeAcceptor an acceptor that accepts the result outcome
     */
    @Open
    public void startTraining(
            Data<TI> inputData,
            TP parameters,
            ActoDataBaseBehavior context,
            Consumer<TrainingOutcome> outcomeAcceptor
    ) {
        inputData.getData(context).then(data -> {
            startTraining(data, parameters, outcomeAcceptor);
        }).onError(err -> {
            outcomeAcceptor.accept(new TrainingError(
                    "Interaction error occurred:" + err));
        }).compel();
    }

    /**
     * Stops the training task, if it is executing
     *
     * @param force indicates the request to forcibly stop the task
     */
    public abstract void stopTraining(boolean force);

    /**
     * @return true if the engine is currently training
     */
    public abstract boolean isTraining();

    /**
     * @return true if the engine can start a training task
     */
    public abstract boolean isReadyForTraining();

    /**
     * @return true if the training task can be stopped.
     */
    protected abstract boolean canTrainingBeStopped();


}
