package it.unipr.sowide.actodata.core.dataset.content;

import it.unipr.sowide.actodata.core.dataset.AcquirableDatasetManager;
import it.unipr.sowide.actodes.interaction.Request;

/**
 * Message used to request an {@link AcquirableDatasetManager} to spawn
 * an acquirer that submits the instances in the dataset.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public enum SpawnAcquirer implements Request {
    INSTANCE
}
