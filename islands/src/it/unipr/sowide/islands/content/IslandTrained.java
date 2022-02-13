package it.unipr.sowide.islands.content;

import it.unipr.sowide.actodata.core.engine.trainable.content.TrainingDone;
import it.unipr.sowide.islands.IslandIndividual;
import it.unipr.sowide.islands.Evaluation;

/**
 * Message used by island engines to notify the controller that the island has
 * completed an iteration of the evolution algorithm and the individuals have
 * been validated against the validation set. The message contains also the
 * best validated individuals found on the island with its fitness.
 *
 */
public class IslandTrained extends TrainingDone {
    public final Evaluation bestEvaluation;

    /**
     * A message to signal that the island has done training.
     *
     * @param bestEvaluation                the best found individual on the
     *                                       validation set
     */
    public IslandTrained(Evaluation bestEvaluation) {
        this.bestEvaluation = bestEvaluation;
    }
}
