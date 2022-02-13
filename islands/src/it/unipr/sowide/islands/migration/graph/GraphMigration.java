package it.unipr.sowide.islands.migration.graph;

import it.unipr.sowide.actodata.core.actodesext.ActoDataBaseBehavior;
import it.unipr.sowide.actodata.core.actodesext.promise.ActoPromise;
import it.unipr.sowide.actodes.interaction.Done;
import it.unipr.sowide.actodes.registry.Reference;
import it.unipr.sowide.islands.migration.MigrationSystem;
import it.unipr.sowide.islands.settings.IslandsSimulationSettings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class GraphMigration extends MigrationSystem {

    private final Graph graph;

    public GraphMigration(IslandsSimulationSettings settings) {
        this.graph = settings.grapStructure;
    }

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

        // For each arc in the graph
        for (Arc arc : graph.getArcs()) {
            addMigrationPromise(
                    migrationPromises,
                    actoDataBaseBehavior,
                    migratingIslands.get(arc.originIndex),
                    migratingIslands.get(arc.destinationIndex),
                    individualsPercentage,
                    instancePercentage
            );
        }

        return migrationPromises;
    }
}
