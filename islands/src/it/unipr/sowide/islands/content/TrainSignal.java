package it.unipr.sowide.islands.content;

import it.unipr.sowide.actodes.interaction.Request;
import it.unipr.sowide.util.annotations.Singleton;

/**
 * Message used by the controller as a "start training" value to tell an island
 * to perform an iteration of the evolution.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
@Singleton
public enum TrainSignal implements Request {
    INSTANCE
}
