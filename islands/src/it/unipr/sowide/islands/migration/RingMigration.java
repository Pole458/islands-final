package it.unipr.sowide.islands.migration;

import it.unipr.sowide.actodata.core.actodesext.ActoDataBaseBehavior;
import it.unipr.sowide.actodata.core.actodesext.promise.ActoPromise;
import it.unipr.sowide.actodes.interaction.Done;
import it.unipr.sowide.actodes.registry.Reference;
import it.unipr.sowide.util.RandomUtils;

import java.util.*;

public class RingMigration extends MigrationSystem {

    private final RandomUtils random;
    private final boolean permutateRing;

    public RingMigration(RandomUtils random, boolean permutateRing) {
        this.random = random;
        this.permutateRing = permutateRing;
    }

    @Override
    public Collection<ActoPromise<? extends Done>> getMigrationPromises(
            ActoDataBaseBehavior actoDataBaseBehavior,
            Set<Reference> references,
            int individualsPercentage,
            int instancePercentage
    ) {

        // will be populated of promises for the actual migration requests
        ArrayList<ActoPromise<? extends Done>> migrationPromises = new ArrayList<>();
        // the islands that shloud migrate
        List<Reference> migratingIslands = new ArrayList<>(references);

        // if a permutation is required
        if (permutateRing) {
            // shuffle randomly
            Collections.shuffle(migratingIslands, random.getJavaRandom());
        }
        else {
            // otherwise, sort according the reference name of the island
            // to guarantee that the ring never changes
            migratingIslands.sort((o1, o2) ->
                    String.CASE_INSENSITIVE_ORDER.compare(
                            o1.getName(),
                            o2.getName()
                    )
            );
        }

        // there must be at least an island...
        if (migratingIslands.size() < 2) {
            return migrationPromises;
        }

        /*
          for each island, create a promise with the request to migrate
          the individual to the next island; the last island is
          skipped
         */
        for (int i = 1; i < migratingIslands.size(); i++) {
            Reference originIsland = migratingIslands.get(i - 1);
            Reference destinationIsland = migratingIslands.get(i);

            addMigrationPromise(
                    migrationPromises,
                    actoDataBaseBehavior,
                    originIsland,
                    destinationIsland,
                    individualsPercentage,
                    instancePercentage
            );

        }

        /*
         does the same thing for the last island, that migrates its
         individuals and/or the data to the first island
         */
        Reference lastIsland = migratingIslands.get(
                migratingIslands.size() - 1);
        Reference firstIsland = migratingIslands.get(0);

        addMigrationPromise(
                migrationPromises,
                actoDataBaseBehavior,
                lastIsland,
                firstIsland,
                individualsPercentage,
                instancePercentage
        );

        return migrationPromises;
    }
}
