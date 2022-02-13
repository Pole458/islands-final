package it.unipr.sowide.islands.settings;

import it.unipr.sowide.islands.migration.MigrationSystemType;
import it.unipr.sowide.islands.migration.graph.Arc;
import it.unipr.sowide.islands.migration.graph.Graph;
import it.unipr.sowide.util.Require;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


/**
 * Container for a set of parameters used for a simulation of SMCGP-Islands.
 *
 */
public class IslandsSimulationSettings {
    /**
     * The name of the simulation. At the end of the simulation, the results
     * of each run be saved in a file called
     * {@code [simulationName]_RUN[runId].txt}.
     *
     * Required.
     */
    public final String simulationName;

    /**
     * The name of the group of islands being evolved. It appears in the GUI and
     * in the logs.
     *
     * Required.
     */
    public final String islandGroupName;

    /**
     * How many runs of this simulation to execute.
     *
     * (default: 1)
     */
    public int numberOfRuns = 1;

    /**
     * The number of islands.
     *
     * (default: 8)
     */
    public int numIslands = 8;

    /**
     * The size of starting population of each island.
     *
     * (default: 500)
     */
    public int islandStartingPopulation = 500;

    /**
     * The number of maximum generations to evolve in each run.
     *
     * (default: 400)
     */
    public int maxGenerations = 400;

    /**
     * Stop the simulation when and individual reaches (>=) the target fitness
     *
     * (default: 1.0)
     */
    public double targetFitness = 1.0;

    /**
     * A migration is performed each {@code migrationRate} generations.
     * If 0, no migrations are executed.
     * <p>
     * (default: 0)
     */
    public int migrationRate = 0;

    /**
     * How many individuals are sent from an island to another at each
     * migration. Considered only if {@code migrationRate} > 0.
     * <p>
     * (default: 10)
     */
    public int individualsMigration = 10;

    /**
     * How many istances of data are sent from an island to another at each
     * migration (significant only if {@code migrationMode} is
     * {@code WITH_DATA_SUBSET} and {@code migrationRate} > 0).
     * <p>
     * (default: 10)
     */
    public int instancesMigration = 10;

    /**
     * How to split the training environment between the islands at the
     * beginning of the simulation.
     *
     * (default: NONE)
     */
    public EnvironmentStartingSplit startingSplit = EnvironmentStartingSplit.NONE;

    /**
     * How to split the training environment between the islands at eachmigration.
     *
     * (default: RANDOM)
     */
    public EnvironmentSplit environmentSplit = EnvironmentSplit.RANDOM;

    /**
     * Type of migration system.
     *
     * (default: RING)
     */
    public MigrationSystemType migrationSystemType = MigrationSystemType.RING;

    /**
     * Wheather or not to permutate the island while using Ring Migration Systems.
     *
     * (defualt: false)
     */
    public boolean permutateIslands = false;

    /**
     * Graph structure used by Graph Migration System
     *
     * (default: no arcs)
     */
    public Graph grapStructure = new Graph(new ArrayList<>());

    public IslandsSimulationSettings(String simulationName, String islandGroupName) {
        this.simulationName = simulationName;
        this.islandGroupName = islandGroupName;
    }

    public static IslandsSimulationSettings parseFromFile(File file)
            throws IOException {
        Properties properties = new Properties();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        properties.load(reader);
        String simulationName = properties.getProperty("simulationName");
        String groupName = properties.getProperty("islandGroupName");
        Require.nonNull(simulationName, groupName);
        IslandsSimulationSettings settings = new IslandsSimulationSettings(
                simulationName,
                groupName
        );
        String numberOfRunsText = properties.getProperty("numberOfRuns");
        if (numberOfRunsText != null) {
            int numberOfRuns = Integer.parseInt(numberOfRunsText);
            Require.strictlyPositive(numberOfRuns, "numberOfRuns");
            settings.numberOfRuns = numberOfRuns;
        }
        String targetFitnessText = properties.getProperty("targetFitness");
        if (targetFitnessText != null) {
            settings.targetFitness = Double.parseDouble(targetFitnessText);
        }
        String migrationRateText = properties.getProperty("migrationRate");
        if (migrationRateText != null) {
            int migrationRate = Integer.parseInt(migrationRateText);
            Require.nonNegative(migrationRate, "migrationRate");
            settings.migrationRate = migrationRate;

        }
        String individualsMigrationText = properties.getProperty("individualsMigration");
        if (individualsMigrationText != null) {
            int individualsMigration = Integer.parseInt(individualsMigrationText);
            Require.nonNegative(individualsMigration, "individualsMigration");
            settings.individualsMigration = individualsMigration;
        }
        String instancesMigrationText = properties.getProperty("instancesMigration");
        if (instancesMigrationText != null) {
            int instancesMigration = Integer.parseInt(instancesMigrationText);
            Require.nonNegative(instancesMigration, "instancesMigration");
            settings.instancesMigration = instancesMigration;
        }
        String startingSplitText = properties.getProperty("startingSplit");
        if (startingSplitText != null) {
            Require.inEnum(EnvironmentStartingSplit.class, startingSplitText, "startingSplit");
            settings.startingSplit = EnvironmentStartingSplit.valueOf(startingSplitText);
        }
        String environmentSplitText = properties.getProperty("environmentSplit");
        if (environmentSplitText != null) {
            Require.inEnum(EnvironmentSplit.class, environmentSplitText, "environmentSplit");
            settings.environmentSplit = EnvironmentSplit.valueOf(environmentSplitText);
        }
        String numIslandsText = properties.getProperty("numIslands");
        if (numIslandsText != null) {
            int numIslands = Integer.parseInt(numIslandsText);
            Require.strictlyPositive(numIslands, "numIslands");
            settings.numIslands = numIslands;
        }
        String islandStartingPopulationText = properties.getProperty("islandStartingPopulation");
        if (islandStartingPopulationText != null) {
            int islandStartingPopulation = Integer.parseInt(islandStartingPopulationText);
            Require.strictlyPositive(islandStartingPopulation, "islandStartingPopulation");
            settings.islandStartingPopulation = islandStartingPopulation;
        }
        String maxGenerationsText = properties.getProperty("maxGenerations");
        if (maxGenerationsText != null) {
            int maxGenerations = Integer.parseInt(maxGenerationsText);
            Require.strictlyPositive(maxGenerations, "maxGenerations");
            settings.maxGenerations = maxGenerations;
        }

        String migrationSystemText = properties.getProperty("migrationSystem");
        if(migrationSystemText != null) {
            Require.inEnum(MigrationSystemType.class, migrationSystemText, "migrationSystem");
            settings.migrationSystemType = MigrationSystemType.valueOf(migrationSystemText);
        }

        if(settings.migrationSystemType == MigrationSystemType.RING) {
            String permutateIslandsText = properties.getProperty("permutateIslands");
            if(permutateIslandsText != null) {
                settings.permutateIslands = Boolean.parseBoolean(permutateIslandsText);
            }
        }

        if(settings.migrationSystemType == MigrationSystemType.GRAPH) {
            String graphStructureText = properties.getProperty("graphStructure");
            if(graphStructureText != null) {
                String[] arcDescriptors = graphStructureText.split(",");
                List<Arc> arcs = new ArrayList<>();
                for (String arcDescriptor: arcDescriptors) {
                    String[] nodesText = arcDescriptor.split("->");
                    if(nodesText.length != 2) continue;
                    int originIndex = Integer.parseInt(nodesText[0]);
                    int destinationIndex = Integer.parseInt(nodesText[1]);

                    Require.inRange(0, settings.numIslands, originIndex);
                    Require.inRange(0, settings.numIslands, destinationIndex);

                    arcs.add(new Arc(originIndex, destinationIndex));
                }

                settings.grapStructure = new Graph(arcs);
            }
        }

        return settings;
    }
}
