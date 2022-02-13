package it.unipr.sowide.islands;

import it.unipr.sowide.actodata.core.actodesext.promise.ActoPromise;
import it.unipr.sowide.actodata.core.actodesext.promise.ActoPromises;
import it.unipr.sowide.actodata.core.controller.Controller;
import it.unipr.sowide.actodata.core.controller.ControllerCaseFactory;
import it.unipr.sowide.actodata.core.controller.extensions.EvaluatableEngineController;
import it.unipr.sowide.actodata.core.controller.extensions.TrainController;
import it.unipr.sowide.actodata.core.engine.evaluatable.content.EvaluationDone;
import it.unipr.sowide.actodata.core.engine.evaluatable.content.EvaluationOutcome;
import it.unipr.sowide.actodata.core.engine.trainable.content.NoParameters;
import it.unipr.sowide.actodata.core.engine.trainable.content.TrainingDone;
import it.unipr.sowide.actodes.actor.Message;
import it.unipr.sowide.actodes.interaction.Done;
import it.unipr.sowide.actodes.interaction.Error;
import it.unipr.sowide.actodes.registry.Reference;
import it.unipr.sowide.islands.content.*;
import it.unipr.sowide.islands.migration.MigrationSystem;
import it.unipr.sowide.islands.migration.RingMigration;
import it.unipr.sowide.islands.migration.SpreadMigration;
import it.unipr.sowide.islands.migration.graph.GraphMigration;
import it.unipr.sowide.islands.settings.IslandsSimulationSettings;
import it.unipr.sowide.util.Pair;
import it.unipr.sowide.util.RandomUtils;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A controller that coordinates a group of {@link IslandEngine}s and outputs
 * results as are computed to a {@link EvaluationsReporter}.
 * The various phases of the evolution are performed by all the engines
 * togheter in this variant.
 *
 */
public class IslandsController extends Controller
        implements TrainController<TrainSignal, NoParameters>,
        EvaluatableEngineController<IslandEnvironment> {

    private final IslandsSimulationSettings settings;

    /**
     * The test dataset used to evaluate the islands
     */
    private final IslandEnvironment testEnv;

    /**
     * The reference of the {@link EvaluationsReporter}.
     */
    private Reference reporter = null;

    /**
     * How many generations have been executed *globally* by this group of
     * islands
     */
    private int generationCount = 0;

    /**
     * The instant when a simulation started
     */
    private Instant startTime = null;

    /**
     * The fitness on the validation set of the best individual
     */
    private double bestValidatedIndividualFitness = Double.NEGATIVE_INFINITY;

    /**
     * Migration sytem used to manage migrations
     */
    private MigrationSystem migrationSystem;

    /**
     * Creates the controller with the specified settings.
     * @param settings            settings
     * @param testEnv             the test environment used to evaluate the islands
     * @param random              a random generation utility instance
     */
    public IslandsController(
            IslandsSimulationSettings settings,
            IslandEnvironment testEnv,
            RandomUtils random
    ) {
        this.settings = settings;
        this.testEnv = testEnv;

        switch (settings.migrationSystemType) {
            case RING -> this.migrationSystem = new RingMigration(random, settings.permutateIslands);
            case SPREAD -> this.migrationSystem = new SpreadMigration();
            case GRAPH -> this.migrationSystem = new GraphMigration(settings);
        }
    }

    @Override
    public void controllerCases(ControllerCaseFactory d) {
        /*
         assigns ther {@link EvaluationsReporter} to this controller.
         */
        d.onContentOfType(AssignReporter.class, (assignReporter, message) -> {
            reporter = assignReporter.getReference();
            send(message, Done.DONE);
        });

        /*
         starts the loop of the simulation.
         */
        d.onContentOfType(StartIslandsLoop.class, (__, message) -> {
            this.startTime = Instant.now();
            trainIslands(message);
        });
    }

    /**
     * Terminates the training, asking the reporter to save the results and
     * replying {@link Done#DONE} to the requester of the loop.
     *
     * @param loopRequest the message of the request that initiated the loop
     */
    private void terminateTraining(Message loopRequest) {
        if (startTime != null && reporter != null) {
            send(reporter, new SaveResults(
                    startTime,
                    Instant.now(),
                    settings.islandGroupName
            ));
        }
        send(loopRequest, Done.DONE);
    }

    /**
     * The first step of an iteration of the simulation
     *
     * @param loopRequest the message of the request that initiated the loop
     */
    private void trainIslands(Message loopRequest) {
        // if the maximum generations has been reached
        if (this.generationCount >= settings.maxGenerations) {
//            actorLog("Generation Limit Reached");
            // terminate the loop
            terminateTraining(loopRequest);
            return;
        }

        /*
         performs a set of requests and awaits all the responses
         */
        ActoPromises.all(
                /*
                 creates a promise by the request to start the training;
                 one promise for each controlled island engine
                */
                getControlledEngines().getReferences().stream()
                        .map((engineRef) -> {
                            return startTraining(
                                    TrainSignal.INSTANCE,
                                    NoParameters.INSTANCE,
                                    engineRef
                            );
                        }).collect(Collectors.toList())
        ).then(outcomes -> { // when all the engine completed

            // if the outcomes are all succesful
            if (outcomes.stream().allMatch(o -> o instanceof TrainingDone)) {

                // finds the best outcome by the fitness on the validation set
                Optional<IslandTrained> bestOutcomeOpt = outcomes.stream()
                        .filter(o -> o instanceof IslandTrained)
                        .map(o -> (IslandTrained) o)
                        .max(Comparator.comparingDouble(o -> o.bestEvaluation.evaluationFitness));

                if (bestOutcomeOpt.isPresent()) {
                    IslandTrained bestOutcome = bestOutcomeOpt.get();
                    double fit = bestOutcome.bestEvaluation.evaluationFitness;

                    // if there is an improvement:
                    if (fit > bestValidatedIndividualFitness) {
                        bestValidatedIndividualFitness = fit;
                    }
                }

                if (bestValidatedIndividualFitness >= settings.targetFitness) {
//                    actorLog("Target Fitness reached!");
                    // terminate the training.
                    terminateTraining(loopRequest);
                } else {
                    // otherwise, proceed with evaluation.
                    evaluateIslands(loopRequest);
                }
            } else {
                /*
                 some training outcome was not "TrainingDone". replying with
                 an error.
                */
                send(loopRequest, Error.FAILEDEXECUTION);
            }

        }).compel();
    }

    private void evaluateIslands(Message loopRequest) {

        /*
         creates a promise, one for each engine, requesting the engine to
         evaluate its best individual on the provided testDataset
         */
        ActoPromises.all(getControlledEngines().getReferences().stream()
                .map((engineRef) ->
                        evaluateEngine(wrapData(testEnv), engineRef)
                                .map(outcome -> Pair.pair(engineRef, outcome))
                )
                .collect(Collectors.toList())
        ).then(outcomePairs -> { // when all evaluations completed

            // builds the ReportEvaluations message...
            Map<Reference, Evaluation> evaluations = new HashMap<>();
            Map<Reference, String> groups = new HashMap<>();
            for (Pair<Reference, EvaluationOutcome> pair : outcomePairs) {
                Reference engine = pair.get1();
                EvaluationOutcome outcome = pair.get2();

                groups.put(engine, settings.islandGroupName);
                if (outcome instanceof EvaluationDone) {
                    EvaluationDone<?> evaluationDone = (EvaluationDone<?>) outcome;

                    evaluations.put(engine, (Evaluation) evaluationDone.getEvaluationData());
                }
            }

            // ... and sends it to the reporter
            if (reporter != null) {
                send(reporter, new ReportEvaluations(evaluations, groups));
            }

            if (outcomePairs.stream()
                    .allMatch(o -> o.get2() instanceof EvaluationDone)) {

                // perform the migrations
                performMigration(loopRequest);
            }
        }).compel();
    }

    private void performMigration(Message loopRequest) {
        generationCount++; // updates the generation count

        /*
         if, according the migrationRate, this is an iteration where the
         migration should be performed:
        */
        if (settings.numIslands >= 2 && settings.migrationRate > 0 && generationCount % settings.migrationRate == 0) {
//            actorLog("MIGRATING...");

            Collection<ActoPromise<? extends Done>> migrationPromises = migrationSystem.getMigrationPromises(
                    this,
                    getControlledEngines().getReferences(),
                    settings.individualsMigration,
                    settings.instancesMigration
            );

            // compels all the built migration promises
            ActoPromises.all(
                    migrationPromises
            ).then((__) -> { // when all are resolved
                trainIslands(loopRequest); // restart the loop (next iteration)
            }).onError((err) -> { // if error, forward it to the requester
                send(loopRequest, err);
            }).compel();
        } else {
            // skipping the migration in this iteration.
            trainIslands(loopRequest);
        }
    }
}
