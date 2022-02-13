package it.unipr.sowide.actodata.core.master;

import it.unipr.sowide.actodata.core.acquirer.Acquirer;
import it.unipr.sowide.actodata.core.actodesext.ActoDataBaseBehavior;
import it.unipr.sowide.actodata.core.actodesext.ActoDataCaseFactory;
import it.unipr.sowide.actodata.core.controller.Controller;
import it.unipr.sowide.actodata.core.dataset.DataSetManager;
import it.unipr.sowide.actodata.core.engine.Engine;
import it.unipr.sowide.actodata.core.master.content.GlobalStart;
import it.unipr.sowide.actodata.core.master.content.InformMaster;
import it.unipr.sowide.actodata.core.master.nodeinterfaces.*;
import it.unipr.sowide.actodata.core.preprocessor.Preprocessor;
import it.unipr.sowide.actodata.core.reporter.Reporter;
import it.unipr.sowide.actodata.core.initialstructure.ActoDataStructure;
import it.unipr.sowide.util.Streams;
import it.unipr.sowide.actodes.registry.Reference;
import it.unipr.sowide.util.annotations.Open;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Behavior of the actor that:
 * <ul>
 *     <li>is used as initiatior of the application;</li>
 *     <li>creates all the actors as defined in the provided
 *     {@link ActoDataStructure};</li>
 *     <li>coordinates global and structural events during the application
 *     execution.</li>
 * </ul>
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class Master extends ActoDataBaseBehavior {

    private final ActoDataStructure structure;
    private final Map<Reference, NodeInterface> interfaceMap
            = new HashMap<>();
    private final Map<String, AcquirerInterface> namedAcquirers
            = new HashMap<>();
    private final Map<String, PreprocessorInterface> namedPreprocessors
            = new HashMap<>();
    private final Map<String, EngineInterface> namedEngines
            = new HashMap<>();
    private final Map<String, ControllerInterface> namedControllers
            = new HashMap<>();
    private final Map<String, DataSetManagerInterface> namedDataSetManagers
            = new HashMap<>();
    private final Map<String, ReporterInterface> namedReporters
            = new HashMap<>();


    private final Timer timer = new Timer();

    /**
     * Creates the master behavior.
     *
     * @param structure the initial structure of the ActoDatA application
     */
    public Master(ActoDataStructure structure) {
        this.structure = structure;
    }


    /**
     * {@inheritDoc}
     **/
    @Override
    public final void actoDataNodeCases(ActoDataCaseFactory c) {
        c.onContentOfType(InformMaster.class, (event, message) -> {
            NodeInterface nodeInterface = interfaceMap.get(message.getSender());
            if (nodeInterface != null) {
                onInform(event, nodeInterface);
            }
        });

        this.masterCases(c);
    }

    /**
     * Defines some specific cases (message pattern/handler pairs) for this
     * master.
     *
     * @param c the case factory
     */
    @Open
    public void masterCases(ActoDataCaseFactory c) {
        // override if needed
    }

    /**
     * {@inheritDoc}
     **/
    @Open
    public final void onStart() {
        actorLog("Master Started.");
        Map<Reference, NodeInterface> interfaceMap =
                structure.buildStructure(this);

        this.interfaceMap.putAll(interfaceMap);

        interfaceMap.values().stream()
                .filter(NodeInterface::isNamed)
                .forEach(interf -> {
                    if (interf instanceof AcquirerInterface) {
                        this.namedAcquirers.put(
                                interf.getName(),
                                (AcquirerInterface) interf
                        );
                    } else if (interf instanceof PreprocessorInterface) {
                        this.namedPreprocessors.put(
                                interf.getName(),
                                (PreprocessorInterface) interf
                        );
                    } else if (interf instanceof EngineInterface) {
                        this.namedEngines.put(
                                interf.getName(),
                                (EngineInterface) interf
                        );
                    } else if (interf instanceof ControllerInterface) {
                        this.namedControllers.put(
                                interf.getName(),
                                (ControllerInterface) interf
                        );
                    } else if (interf instanceof DataSetManagerInterface) {
                        this.namedDataSetManagers.put(
                                interf.getName(),
                                (DataSetManagerInterface) interf
                        );
                    } else if (interf instanceof ReporterInterface) {
                        this.namedReporters.put(
                                interf.getName(),
                                (ReporterInterface) interf
                        );
                    }
                });

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                for (var reference : Master.this.interfaceMap.keySet()) {
                    send(reference, new GlobalStart(getReference()));
                }
                timer.cancel();

                Master.this.onGlobalStart();
            }
        }, 1000);
    }

    /**
     * Defines what to do when a message with content that implements
     * {@link InformMaster} from an known actor is received by this master.
     *
     * @param informMaster the message content
     * @param source       the {@link NodeInterface} of the sender
     */
    @Open
    public void onInform(InformMaster informMaster, NodeInterface source) {
        // override if needed
    }

    /**
     * Defines what to do after that this master has started up and notified
     * all the actors in the initial structure that the application started with
     * the {@link GlobalStart} message.
     */
    @Open
    public void onGlobalStart() {
        // override if needed
    }

    /**
     * @return the initial structure
     */
    public ActoDataStructure getStructure() {
        return this.structure;
    }

    /**
     * @return a collection of all the {@link NodeInterface}s for the main
     * actors known to be part of this ActoDatA application.
     */
    public Collection<NodeInterface> getInterfaces() {
        return interfaceMap.values();
    }

    /**
     * If the {@link NodeInterface} with the specified name is known by this
     * master, returns an {@link Optional} containing it.
     *
     * @param name the name of the actor
     * @return an {@link Optional} containing the {@link NodeInterface},
     * {@link Optional#empty()} otherwise.
     */
    public Optional<NodeInterface> getInterfaceByName(String name) {
        Optional<? extends NodeInterface> o = List.of(
                (Supplier<Optional<? extends NodeInterface>>)
                        () -> getAcquirerByName(name),
                () -> getPreprocessorByName(name),
                () -> getEngineByName(name),
                () -> getControllerByName(name),
                () -> getDataSetManagerByName(name),
                () -> getReporterByName(name)
        ).stream()
                .map(Supplier::get)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findAny();

        return o.map(ni -> (NodeInterface) ni);
    }


    /**
     * @return all the interfaces of the Acquirers known by this master.
     */
    public List<AcquirerInterface> getAcquirers() {
        return getInterfaces().stream()
                .flatMap(Streams.filterCast(AcquirerInterface.class))
                .collect(Collectors.toList());
    }

    /**
     * @return all the interfaces of the Preprocessors known by this master.
     */
    public List<PreprocessorInterface> getPreprocessors() {
        return getInterfaces().stream()
                .flatMap(Streams.filterCast(PreprocessorInterface.class))
                .collect(Collectors.toList());
    }

    /**
     * @return all the interfaces of the Engines known by this master.
     */
    public List<EngineInterface> getEngines() {
        return getInterfaces().stream()
                .flatMap(Streams.filterCast(EngineInterface.class))
                .collect(Collectors.toList());
    }

    /**
     * @return all the interfaces of the Controllers known by this master.
     */
    public List<ControllerInterface> getControllers() {
        return getInterfaces().stream()
                .flatMap(Streams.filterCast(ControllerInterface.class))
                .collect(Collectors.toList());
    }

    /**
     * @return all the interfaces of the DataSetManagers known by this master.
     */
    public List<DataSetManagerInterface> getDataSetManagers() {
        return getInterfaces().stream()
                .flatMap(Streams.filterCast(DataSetManagerInterface.class))
                .collect(Collectors.toList());
    }

    /**
     * @return all the interfaces of the Reporters known by this master.
     */
    public List<ReporterInterface> getReporters() {
        return getInterfaces().stream()
                .flatMap(Streams.filterCast(ReporterInterface.class))
                .collect(Collectors.toList());
    }

    /**
     * If the {@link NodeInterface} with the specified name is known by this
     * master, returns an {@link Optional} containing it.
     *
     * @param name the name of the actor
     * @return an {@link Optional} containing the {@link NodeInterface},
     * {@link Optional#empty()} otherwise.
     */
    public Optional<AcquirerInterface> getAcquirerByName(String name) {
        return Optional.ofNullable(namedAcquirers.get(name));
    }

    /**
     * If the {@link NodeInterface} with the specified name is known by this
     * master, returns an {@link Optional} containing it.
     *
     * @param name the name of the actor
     * @return an {@link Optional} containing the {@link NodeInterface},
     * {@link Optional#empty()} otherwise.
     */
    public Optional<PreprocessorInterface> getPreprocessorByName(String name) {
        return Optional.ofNullable(namedPreprocessors.get(name));
    }

    /**
     * If the {@link NodeInterface} with the specified name is known by this
     * master, returns an {@link Optional} containing it.
     *
     * @param name the name of the actor
     * @return an {@link Optional} containing the {@link NodeInterface},
     * {@link Optional#empty()} otherwise.
     */
    public Optional<EngineInterface> getEngineByName(String name) {
        return Optional.ofNullable(namedEngines.get(name));
    }

    /**
     * If the {@link NodeInterface} with the specified name is known by this
     * master, returns an {@link Optional} containing it.
     *
     * @param name the name of the actor
     * @return an {@link Optional} containing the {@link NodeInterface},
     * {@link Optional#empty()} otherwise.
     */
    public Optional<ControllerInterface> getControllerByName(String name) {
        return Optional.ofNullable(namedControllers.get(name));
    }

    /**
     * If the {@link NodeInterface} with the specified name is known by this
     * master, returns an {@link Optional} containing it.
     *
     * @param name the name of the actor
     * @return an {@link Optional} containing the {@link NodeInterface},
     * {@link Optional#empty()} otherwise.
     */
    public Optional<DataSetManagerInterface> getDataSetManagerByName(
            String name
    ) {
        return Optional.ofNullable(namedDataSetManagers.get(name));
    }

    /**
     * If the {@link NodeInterface} with the specified name is known by this
     * master, returns an {@link Optional} containing it.
     *
     * @param name the name of the actor
     * @return an {@link Optional} containing the {@link NodeInterface},
     * {@link Optional#empty()} otherwise.
     */
    public Optional<ReporterInterface> getReporterByName(String name) {
        return Optional.ofNullable(namedReporters.get(name));
    }

    /**
     * Creates a new acquirer with the provided behavior.
     *
     * @param behavior the behavior
     * @return the interface that can be used to interact with the actor from
     * this master.
     */
    public AcquirerInterface createAcquirer(Acquirer<?> behavior) {
        Reference ref = actor(behavior);
        var interf = new AcquirerInterface(this, ref, behavior.getClass());
        interfaceMap.put(ref, interf);
        return interf;
    }

    /**
     * Creates a new preprocessor with the provided behavior.
     *
     * @param behavior the behavior
     * @return the interface that can be used to interact with the actor from
     * this master.
     */
    public PreprocessorInterface createPreprocessor(
            Preprocessor<?, ?> behavior
    ) {
        Reference ref = actor(behavior);
        var interf = new PreprocessorInterface(this, ref, behavior.getClass());
        interfaceMap.put(ref, interf);
        return interf;
    }

    /**
     * Creates a new controller with the provided behavior.
     *
     * @param behavior the behavior
     * @return the interface that can be used to interact with the actor from
     * this master.
     */
    public ControllerInterface createController(Controller behavior) {
        Reference ref = actor(behavior);
        var interf = new ControllerInterface(this, ref, behavior.getClass());
        interfaceMap.put(ref, interf);
        return interf;
    }

    /**
     * Creates a new engine with the provided behavior.
     *
     * @param behavior the behavior
     * @return the interface that can be used to interact with the actor from
     * this master.
     */
    public EngineInterface createEngine(Engine behavior) {
        Reference ref = actor(behavior);
        var interf = new EngineInterface(this, ref, behavior.getClass());
        interfaceMap.put(ref, interf);
        return interf;
    }

    /**
     * Creates a new dataset manager with the provided behavior.
     *
     * @param behavior the behavior
     * @return the interface that can be used to interact with the actor from
     * this master.
     */
    public DataSetManagerInterface createDataSetManager(
            DataSetManager<?, ?> behavior
    ) {
        Reference ref = actor(behavior);
        var interf = new DataSetManagerInterface(this, ref, behavior.getClass());
        interfaceMap.put(ref, interf);
        return interf;
    }

    /**
     * Creates a new reporter with the provided behavior.
     *
     * @param behavior the behavior
     * @return the interface that can be used to interact with the actor from
     * this master.
     */
    public ReporterInterface createReporter(Reporter<?> behavior) {
        Reference ref = actor(behavior);
        var interf = new ReporterInterface(this, ref, behavior.getClass());
        interfaceMap.put(ref, interf);
        return interf;
    }

    /**
     * Creates a new acquirer with the provided behavior and name.
     *
     * @param behavior the behavior
     * @param name     the name
     * @return the interface that can be used to interact with the actor from
     * this master.
     */
    public AcquirerInterface createAcquirer(String name, Acquirer<?> behavior) {
        var interf = createAcquirer(behavior);
        namedAcquirers.put(name, interf);
        return interf;
    }

    /**
     * Creates a new preprocessor with the provided behavior and name.
     *
     * @param behavior the behavior
     * @param name     the name
     * @return the interface that can be used to interact with the actor from
     * this master.
     */
    public PreprocessorInterface createPreprocessor(
            String name,
            Preprocessor<?, ?> behavior
    ) {
        var interf = createPreprocessor(behavior);
        namedPreprocessors.put(name, interf);
        return interf;
    }

    /**
     * Creates a new controller with the provided behavior and name.
     *
     * @param behavior the behavior
     * @param name     the name
     * @return the interface that can be used to interact with the actor from
     * this master.
     */
    public ControllerInterface createController(
            String name,
            Controller behavior
    ) {
        var interf = createController(behavior);
        namedControllers.put(name, interf);
        return interf;
    }

    /**
     * Creates a new engine with the provided behavior and name.
     *
     * @param behavior the behavior
     * @param name     the name
     * @return the interface that can be used to interact with the actor from
     * this master.
     */
    public EngineInterface createEngine(String name, Engine behavior) {
        var interf = createEngine(behavior);
        namedEngines.put(name, interf);
        return interf;
    }

    /**
     * Creates a new dataset manager with the provided behavior and name.
     *
     * @param behavior the behavior
     * @param name     the name
     * @return the interface that can be used to interact with the actor from
     * this master.
     */
    public DataSetManagerInterface createDataSetManager(
            String name,
            DataSetManager<?, ?> behavior
    ) {
        var interf = createDataSetManager(behavior);
        namedDataSetManagers.put(name, interf);
        return interf;
    }

    /**
     * Creates a new reporter with the provided behavior and name.
     *
     * @param behavior the behavior
     * @param name     the name
     * @return the interface that can be used to interact with the actor from
     * this master.
     */
    public ReporterInterface createReporter(String name, Reporter<?> behavior) {
        var interf = createReporter(behavior);
        namedReporters.put(name, interf);
        return interf;
    }

}
