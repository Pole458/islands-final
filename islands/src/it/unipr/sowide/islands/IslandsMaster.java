package it.unipr.sowide.islands;


import it.unipr.sowide.actodata.core.actodesext.promise.ActoPromise;
import it.unipr.sowide.actodata.core.actodesext.promise.ActoPromises;
import it.unipr.sowide.actodata.core.initialstructure.ActoDataStructure;
import it.unipr.sowide.actodata.core.initialstructure.ControllerDescriptor;
import it.unipr.sowide.actodata.core.master.Master;
import it.unipr.sowide.actodata.core.master.nodeinterfaces.ControllerInterface;
import it.unipr.sowide.actodata.core.master.nodeinterfaces.NodeInterface;
import it.unipr.sowide.actodes.configuration.Configuration;
import it.unipr.sowide.actodes.controller.SpaceInfo;
import it.unipr.sowide.actodes.executor.active.ThreadCoordinator;
import it.unipr.sowide.actodes.interaction.Done;
import it.unipr.sowide.actodes.interaction.Error;
import it.unipr.sowide.actodes.interaction.Kill;
import it.unipr.sowide.actodes.registry.Reference;
import it.unipr.sowide.actodes.service.logging.ConsoleWriter;
import it.unipr.sowide.actodes.service.logging.Logger;
import it.unipr.sowide.actodes.service.logging.TextualFormatter;
import it.unipr.sowide.actodes.service.logging.util.NoCycleProcessing;
import it.unipr.sowide.islands.content.AssignReporter;
import it.unipr.sowide.islands.content.StartIslandsLoop;
import it.unipr.sowide.islands.settings.EnvironmentStartingSplit;
import it.unipr.sowide.islands.settings.IslandsSimulationSettings;
import it.unipr.sowide.util.RandomUtils;
import it.unipr.sowide.util.promise.Promise;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * An initiator/master that creates a controller and set of island-engines for
 * each simulation defined, according to the set of the loaded
 * {@link IslandsSimulationSettings}.
 */
public abstract class IslandsMaster extends Master {

    protected IslandEnvironment trainEnv;
    protected IslandEnvironment validationEnv;
    protected IslandEnvironment testEnv;

    // if true, the EvaluationsReporter shows a gui with real-time graphs
    protected boolean showGui;
    private IslandsSimulationSettings settings;

    public IslandsMaster(ActoDataStructure structure) {
        super(structure);
    }

    @Override
    public void onGlobalStart() {
        super.onGlobalStart();

        // keeps the reference for a reporter, which is created.
        AtomicReference<Reference> reporter = new AtomicReference<>(
                actor(new EvaluationsReporter(settings, showGui))
        );

        /*
         will be filled with the promises; each promise represents the request
         of the master to a controller to perform the simulation loop;
         at the end, all the promises are compelled sequentially.
        */
        List<Promise<Done, Error>> promises = new ArrayList<>();

        var i = 0;

        List<ControllerInterface> controllers =
                new ArrayList<>(getControllers());

        // controllers (and therefore simulations) are sorted by their name
        controllers.sort(Comparator.comparing(NodeInterface::getName));

        // builds a promise for each controller
        for (ControllerInterface controller : controllers) {
            int finalI = i;
            promises.add(
                    new ActoPromise<Done>((rs, rj) -> rs.resolve(Done.DONE)
                    ).thenAwait((__) -> {
                        // tells the controller to use its dedicated reporter
                        return promiseFuture(
                                controller.getReference(),
                                new AssignReporter(reporter.get()),
                                Done.class
                        );
                        //then awaits for Done.DONE...
                    }).thenAwait((__) -> {
                        // then asks the controller to start the loop
                        return promiseFuture(
                                controller.getReference(),
                                StartIslandsLoop.INSTANCE,
                                Done.class
                        );
                        //then awaits for Done.DONE...
                    }).then((__) -> {
                        //then, if we are not talking with the last controller
                        if (finalI < controllers.size() - 1) {
                            // asks the reporter to kill himself
                            send(reporter.get(), Kill.KILL);
                            // creates a new reporter for the next controller
                            reporter.set(
                                    actor(new EvaluationsReporter(settings, showGui))
                            );
                        }
                    })
            );
            i++;
        }

        // executes all the promises, in sequence (not in parallel!)
        ActoPromises.sequentially(promises)
//                .then((__) -> actorLog("ALL RUNS COMPLETED!"))
                .compel();
    }

    /**
     * Required:<ul>
     * <li>config-file: the .properties file that defines the simulation;
     * <li>trainset-file: the training set;
     * <li>validationset-file: the validation set;
     * <li>testset-file: the test set;
     * </ul>
     * <p>
     * Optionally, use "-gui" as first option to activate the reporters' gui.
     */
    protected abstract String usageString();

    protected abstract boolean checkNumberOfArguemnts(String[] argv);

    protected void unexpectedArguments() {
        System.out.println("Unexpected arguments.");
        System.out.println(usageString());
        System.exit(1);
    }

    protected void parseFileArgs(String[] argv) throws IOException {
        for (int i = 0; i < argv.length; i++) {
            switch (argv[i]) {
                case "-gui" -> showGui = true;
                case "-config" -> {
                    if (++i >= argv.length) unexpectedArguments();
                    settings = IslandsSimulationSettings.parseFromFile(new File(argv[i]));
                }
            }
        }
    }

    /**
     * Starts the defined simulations.
     *
     * @param argv (see {@link #usageString()})
     */
    protected void setUp(String[] argv) throws IOException {

        if (!checkNumberOfArguemnts(argv)) unexpectedArguments();

        parseFileArgs(argv);

        createRuns(getStructure(), settings);

        Configuration configuration = SpaceInfo.INFO.getConfiguration();
        configuration.setFilter(Logger.ACTORCREATION | Logger.ACTORSHUTDOWN);
        configuration.setLogFilter(new NoCycleProcessing());
        configuration.addWriter(new ConsoleWriter(new TextualFormatter()));
        configuration.setExecutor(new ThreadCoordinator(this));

        configuration.start();
        System.out.println("Started");
    }

    protected abstract IslandEngine generateIslandEngine(
            IslandEnvironment trainingSet,
            IslandEnvironment validationData,
            IslandsSimulationSettings settings,
            RandomUtils random
    );

    protected void createRuns(
            ActoDataStructure actoDataStructure,
            IslandsSimulationSettings settings
    ) {
        RandomUtils random = new RandomUtils();

        for (int i = 1; i <= settings.numberOfRuns; i++) {
            String runName = settings.simulationName + "_RUN" + i;
            ControllerDescriptor groupController = actoDataStructure.controllerNode(runName, () -> {
                return new IslandsController(
                        settings,
                        testEnv,
                        random
                );
            });

            List<IslandEnvironment> trainEnvironments;
            switch (settings.startingSplit) {
                case RANDOM -> trainEnvironments = trainEnv.startingSplitRandom(settings.numIslands, random);
                case CONTIGUOUS -> trainEnvironments = trainEnv.startingSplitContiguous(settings.numIslands);
                case ROUNDROBIN -> trainEnvironments = trainEnv.startingSplitRoundRobin(settings.numIslands);
                default -> trainEnvironments = new ArrayList<>();
            }

            for (int j = 0; j < settings.numIslands; j++) {
                int finalJ = j;
                groupController.linkEngine(
                        actoDataStructure.engineNode(() -> generateIslandEngine(
                                        settings.startingSplit == EnvironmentStartingSplit.NONE ? trainEnv : trainEnvironments.get(finalJ),
                                        validationEnv,
                                        settings,
                                        random.childRandom()
                                )
                        )
                );
            }
        }
    }
}
