package it.unipr.sowide.islands.migration;

import it.unipr.sowide.actodata.core.actodesext.ActoDataBaseBehavior;
import it.unipr.sowide.actodata.core.actodesext.promise.ActoPromise;
import it.unipr.sowide.actodes.interaction.Done;
import it.unipr.sowide.actodes.registry.Reference;
import it.unipr.sowide.islands.content.MigrationData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

public abstract class MigrationSystem {

    public abstract Collection<ActoPromise<? extends Done>> getMigrationPromises(
            ActoDataBaseBehavior actoDataBaseBehavior,
            Set<Reference> references,
            int individualsPercentage,
            int instancePercentage
    );

    protected void addMigrationPromise(
            ArrayList<ActoPromise<? extends Done>> migrationPromises,
            ActoDataBaseBehavior actoDataBaseBehavior,
            Reference originIsland,
            Reference destinationIsland,
            int individualsPercentage,
            int instancesPercentage
    ) {
        migrationPromises.add(actoDataBaseBehavior.promiseFuture(
                originIsland,
                new MigrationData(
                        destinationIsland,
                        individualsPercentage,
                        instancesPercentage
                )
        ));
    }
}
