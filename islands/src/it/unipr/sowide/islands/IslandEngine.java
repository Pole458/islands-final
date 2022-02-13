package it.unipr.sowide.islands;

import it.unipr.sowide.actodata.core.actodesext.ActoDataCaseFactory;
import it.unipr.sowide.actodata.core.actodesext.promise.ActoPromise;
import it.unipr.sowide.actodata.core.actodesext.promise.ActoPromises;
import it.unipr.sowide.actodata.core.engine.Engine;
import it.unipr.sowide.actodata.core.engine.EngineComponent;
import it.unipr.sowide.actodata.core.engine.evaluatable.EvaluatableEngineComponent;
import it.unipr.sowide.actodata.core.engine.evaluatable.content.EvaluationDone;
import it.unipr.sowide.actodata.core.engine.evaluatable.content.EvaluationOutcome;
import it.unipr.sowide.actodata.core.engine.trainable.TrainableEngineComponent;
import it.unipr.sowide.actodata.core.engine.trainable.content.NoParameters;
import it.unipr.sowide.actodata.core.engine.trainable.content.TrainingOutcome;
import it.unipr.sowide.actodes.interaction.Done;
import it.unipr.sowide.gpj.core.Evolution;
import it.unipr.sowide.gpj.core.EvolutionContext;
import it.unipr.sowide.gpj.core.IndividualGenerator;
import it.unipr.sowide.gpj.core.breeding.FillingReproduction;
import it.unipr.sowide.gpj.core.breeding.PreserveIndividuals;
import it.unipr.sowide.gpj.core.breeding.SequentialBreeding;
import it.unipr.sowide.gpj.core.breeding.mating.MateOperator;
import it.unipr.sowide.gpj.core.breeding.mating.Mating;
import it.unipr.sowide.gpj.core.breeding.mating.StocasticMateOperator;
import it.unipr.sowide.gpj.core.breeding.mutation.Mutation;
import it.unipr.sowide.gpj.core.breeding.mutation.MutationOperator;
import it.unipr.sowide.gpj.core.breeding.mutation.StocasticMutationOperator;
import it.unipr.sowide.gpj.core.breeding.selection.DistinctRandomSelection;
import it.unipr.sowide.gpj.core.breeding.selection.EliteSelection;
import it.unipr.sowide.gpj.core.breeding.selection.RandomSelection;
import it.unipr.sowide.gpj.core.breeding.selection.Selection;
import it.unipr.sowide.gpj.core.evaluation.SyncParallelPopEvaluator;
import it.unipr.sowide.islands.content.IslandTrained;
import it.unipr.sowide.islands.content.Migration;
import it.unipr.sowide.islands.content.MigrationData;
import it.unipr.sowide.islands.content.TrainSignal;
import it.unipr.sowide.islands.evolution.TournamentSelection;
import it.unipr.sowide.islands.settings.EnvironmentSplit;
import it.unipr.sowide.islands.settings.IslandsSimulationSettings;
import it.unipr.sowide.util.RandomUtils;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public abstract class IslandEngine
        extends Engine
        implements IndividualGenerator<IslandIndividual>, MateOperator<IslandIndividual>, MutationOperator<IslandIndividual> {

    protected final RandomUtils random;
    protected Evolution<IslandIndividual> evolution;

    private final IslandsSimulationSettings settings;

    /**
     * the dataset used to compute the fitnesses of the evolved trees
     */
    private final IslandEnvironment trainingEnv;

    /**
     * the dataset used to validate the individuals after each iteration
     */
    private final IslandEnvironment validationEnv;

    /**
     * size of the tournaments used in the tournament selection
     */
    protected final int tournSize = 7;

    /**
     * probability that two selected individuals perform a crossover during the
     * breeding phase
     */
    protected final double cxPb = 0.8;

    /**
     * probability that a selected individual performs a mutation during the
     * breeding phase
     */
    protected final double mutPb = 0.03;

    /**
     * simple evaluation of the individual that performed best on the validation set
     * during the last validation
     */
    private Evaluation bestEvaluation = null;

    private final AtomicBoolean isTraining = new AtomicBoolean(false);

    /**
     * The selection strategy used to select the emigrants
     */
    private final Selection<IslandIndividual> emigrantsSelection = new DistinctRandomSelection<>();

    /**
     * An engine that evolves the classifiers using the specified
     * hyperparameters.
     *
     * @param trainingEnv   the set of data used to evolve the classifiers
     * @param validationEnv the set of data used to validate the
     *                      classifiers
     * @param settings      settings
     * @param random        a random generation utility instance
     */
    public IslandEngine(
            IslandEnvironment trainingEnv,
            IslandEnvironment validationEnv,
            IslandsSimulationSettings settings,
            RandomUtils random
    ) {
        this.trainingEnv = trainingEnv;
        this.validationEnv = validationEnv;
        this.settings = settings;
        this.random = random;
    }

    @Override
    protected void setup() {

        evolution = new Evolution<>(
                this,
                new SyncParallelPopEvaluator<>(individual -> evaluate(individual, trainingEnv)),
                new PreserveIndividuals<>(
                        1,
                        new EliteSelection<>(),
                        new RandomSelection<>(),
                        new SequentialBreeding<IslandIndividual>()
                                .addBreedingPhase(new TournamentSelection<>(tournSize))
                                .addBreedingPhase(new FillingReproduction<>())
                                .addBreedingPhase(new Mating<>(new StocasticMateOperator<>(cxPb, this)))
                                .addBreedingPhase(new Mutation<>(new StocasticMutationOperator<>(mutPb, this)))
                ),
                random,
                settings.islandStartingPopulation
        );

        evolution.initialize();
    }

    public abstract double evaluate(IslandIndividual individual, IslandEnvironment environment);

    @Override
    public IslandIndividual mutate(EvolutionContext evolutionContext, IslandIndividual individual) {
        return individual.mutate(evolutionContext);
    }

    // Components

    @Override
    public Set<EngineComponent> components() {
        return Set.of(
                trainableComponent,
                evaluationComponent
        );
    }

    private final TrainableEngineComponent<TrainSignal, NoParameters> trainableComponent = new TrainableEngineComponent<>() {
        @Override
        public void startTraining(TrainSignal inputData, NoParameters parameters, Consumer<TrainingOutcome> outcomeAcceptor) {
            isTraining.set(true);

            try {
                // executes the actual iteration of evolution
                evolution.syncIteration();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            /*
            evaluate the new population on the validation set
             */
            evolution.getCurrentPopulation().stream()
                    .parallel() // done in parallel
                    .forEach(individual -> {
                        // evaluates an individual
                        Evaluation evaluation =
                                new Evaluation(individual, evaluate(individual, validationEnv), evolution.evaluationsCount.get());

                        /*
                         if the fitness is better than the current best
                         individual, update it and reset the
                         noImprovementsCount.
                         */
                        synchronized (IslandEngine.this) {
                            if(bestEvaluation == null || evaluation.evaluationFitness > bestEvaluation.evaluationFitness) {
                                bestEvaluation = evaluation;
                                // when there actually is an improvement,
                                //   noImprovementsCount will be set to 0...
                            }
                        }

//                        actorLog(settings.islandGroupName + " " + getReference().getName() + "\n"
//                                + bestEvaluation
//                                + "\n"
//                                + trainingEnv
//                        );
                    });

            // when there actually is an improvement,
            //   noImprovementsCount is set to 0:

            // returns the result to the requester
            outcomeAcceptor.accept(new IslandTrained(bestEvaluation));

            isTraining.set(false);
        }

        @Override
        public void stopTraining(boolean force) {
            throw new RuntimeException("The training process is not stoppable");
        }

        @Override
        public boolean isTraining() {
            return isTraining.get();
        }

        @Override
        public boolean isReadyForTraining() {
            return !isTraining.get();
        }

        @Override
        protected boolean canTrainingBeStopped() {
            return false;
        }
    };

    private final EvaluatableEngineComponent<IslandEnvironment> evaluationComponent = new EvaluatableEngineComponent<>() {
        /**
         * It simply evaluates the best validated individual on the provided
         * test set.
         *
         * @param input           the input test set
         * @param outcomeAcceptor the outcome acceptor
         */
        @Override
        public void evaluate(IslandEnvironment input, Consumer<EvaluationOutcome> outcomeAcceptor) {
            IslandIndividual ind = bestEvaluation != null ?
                    bestEvaluation.individual :
                    evolution.getHallOfFame().getBestIndividuals().first();

            Evaluation evaluation =
                    new Evaluation(ind, IslandEngine.this.evaluate(ind, input), evolution.evaluationsCount.get());

            outcomeAcceptor.accept(new EvaluationDone<>(evaluation));
        }
    };

    // Some custom cases for migration mechanics
    @Override
    protected void engineCases(ActoDataCaseFactory c) {
        super.engineCases(c);

        /*
         a set of individuals and/or data arrives to this engine
         */
        c.onContentOfType(Migration.class, (migration, message) -> {

            if (!migration.getIndividuals().isEmpty()) {
//                actorLog("Received " + migration.getIndividuals().size() + " individuals from " + message.getSender().getName());
                evolution.immigrate(migration.getIndividuals());
            }

            // note that no duplicate instances of data are used.
            if (!migration.getInstances().isEmpty()) {
//                actorLog("Received " + migration.getInstances().size() + " instances from " + message.getSender().getName());
                trainingEnv.instances.addAll(migration.getInstances());
            }

            // sends Done.DONE
            done(message);
        });

        /*
         request to migrate some individuals and/or data to another island
         */
        c.onContentOfType(MigrationData.class, (migrate, message) -> {

            // an async computation:
            ActoPromises.Do().thenAwait((__) -> ActoPromise.throwableToFailed(
                    // first, selects the emigrants, and await
                    evolution.emigrate(emigrantsSelection, migrate.individualsNumber)
            )).thenAwait((migrants) -> {
                // then, build the Migration message
                Migration migration = new Migration(
                        migrants,
                        settings.environmentSplit == EnvironmentSplit.RANDOM ?
                                trainingEnv.randomSplit(random, migrate.instancesNumber) :
                                trainingEnv.contiguousSplit(random, migrate.instancesNumber)
                );

                // send the migration message, and await the Done response
                return promiseFuture(
                        migrate.destination,
                        migration,
                        (Done) null
                );
            }).then((__) -> {
                // the destination island responded "Done"
                // replies Done to the requester of the migration
                done(message);
            }).onError(err -> {
                // there was an interaction error; forwards it to the requester
                send(message, err);
            }).compel();
        });

        // replies with the best validated individual
        c.serveProperty(
                "bestValidatedIndividual",
                () -> bestEvaluation.individual
        );
    }
}
