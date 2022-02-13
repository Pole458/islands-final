package it.unipr.sowide.islands.migration;

import it.unipr.sowide.actodata.core.actodesext.ActoDataBaseBehavior;
import it.unipr.sowide.actodata.core.actodesext.promise.ActoPromise;
import it.unipr.sowide.actodes.interaction.Done;
import it.unipr.sowide.actodes.registry.Reference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class SpreadMigration extends MigrationSystem {

    private int currentIslandIndex = 0;

    @Override
    public Collection<ActoPromise<? extends Done>> getMigrationPromises(
            ActoDataBaseBehavior actoDataBaseBehavior,
            Set<Reference> references,
            int individualsPercentage,
            int instancePercentage
    ) {
        // Will be populated of promises for the actual migration requests
        ArrayList<ActoPromise<? extends Done>> migrationPromises = new ArrayList<>();

        // The islands that should migrate
        List<Reference> migratingIslands = new ArrayList<>(references);

        migratingIslands.sort((o1, o2) ->
                String.CASE_INSENSITIVE_ORDER.compare(
                        o1.getName(),
                        o2.getName()
                )
        );

        // Get current spreading island
        Reference spreadingIsland = migratingIslands.get(currentIslandIndex);

        // Increase current island index
        currentIslandIndex = (currentIslandIndex + 1) / references.size();

        // there must be at least two islands
        if (migratingIslands.size() >= 2) {

            for (Reference island : migratingIslands) {

                // Skip spreading island
                if(spreadingIsland == island) continue;

                // Migrate from spreading island to island
                addMigrationPromise(
                        migrationPromises,
                        actoDataBaseBehavior,
                        spreadingIsland,
                        island,
                        individualsPercentage,
                        instancePercentage
                );

                // Migrate from island to spreading island
                addMigrationPromise(
                        migrationPromises,
                        actoDataBaseBehavior,
                        island,
                        spreadingIsland,
                        individualsPercentage,
                        instancePercentage
                );
            }
        }

        return migrationPromises;
    }
}
