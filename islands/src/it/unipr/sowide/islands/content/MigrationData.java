package it.unipr.sowide.islands.content;

import it.unipr.sowide.actodes.interaction.Request;
import it.unipr.sowide.actodes.registry.Reference;

/**
 * Message used by a controller to ask an island engine to initiate a migration.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */


public class MigrationData implements Request {
    public final Reference destination;
    public final int individualsNumber;
    public final int instancesNumber;

    /**
     * @param destination   the island representing the destination of the
     *                      migration
     * @param individualsNumber how many individuals to migrate
     * @param instancesNumber how many instances to migrate
     */
    public MigrationData(
            Reference destination,
            int individualsNumber,
            int instancesNumber
    ) {
        this.individualsNumber = individualsNumber;
        this.instancesNumber = instancesNumber;
        this.destination = destination;
    }
}
