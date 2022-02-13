package it.unipr.sowide.actodata.core.initialstructure;


import it.unipr.sowide.actodata.core.acquirer.Acquirer;
import it.unipr.sowide.actodata.core.controller.content.AssignEngine;
import it.unipr.sowide.actodata.core.controller.Controller;
import it.unipr.sowide.actodata.core.dataflow.content.AssignDataSource;
import it.unipr.sowide.actodata.core.dataset.DataSetManager;
import it.unipr.sowide.actodata.core.engine.Engine;
import it.unipr.sowide.actodata.core.master.Master;
import it.unipr.sowide.actodata.core.master.nodeinterfaces.*;
import it.unipr.sowide.actodata.core.preprocessor.Preprocessor;
import it.unipr.sowide.actodata.core.reporter.Reporter;
import it.unipr.sowide.util.Sets;
import it.unipr.sowide.actodes.actor.Behavior;
import it.unipr.sowide.actodes.registry.Reference;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Utility that helps to define the initial structure of an ActoDatA
 * application. Many methods can be used to declare the existence of needed
 * actors. A completed structure can then be passed to the constructor of a
 * {@link Master} which will create all the declared actors and connect all the
 * declared links between them right at the start of the application.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class ActoDataStructure {

    private final Set<AcquirerDescriptor<?>> acquirerDescriptors
            = new HashSet<>();
    private final Set<PreprocessorDescriptor<?, ?>> preprocessorDescriptors
            = new HashSet<>();
    private final Set<EngineDescriptor> engineDescriptors
            = new HashSet<>();
    private final Set<DataSetManagerDescriptor<?, ?>> dataSetManagerDescriptors
            = new HashSet<>();
    private final Set<ControllerDescriptor> controllerDescriptors
            = new HashSet<>();
    private final Set<ReporterDescriptor<?>> reporterDescriptors
            = new HashSet<>();

    /**
     * Declares an {@link Acquirer}.
     *
     * @param acquirerSupplier the method that will create the behavior
     * @param <T>              the type of the acquired data
     * @return a descriptor of the declared actor, for chain call support.
     */
    public <T> AcquirerDescriptor<T> acquirerNode(
            Supplier<Acquirer<T>> acquirerSupplier
    ) {
        AcquirerDescriptor<T> x = new AcquirerDescriptor<>(acquirerSupplier);
        acquirerDescriptors.add(x);
        return x;
    }

    /**
     * Declares a {@link Preprocessor}
     *
     * @param preprocessorSupplier the method that will create the behavior.
     * @param <I>                  the input data type of the preprocessor
     * @param <O>                  the output data type of the preprocessor
     * @return a descriptor of the declared actor, for chain call support.
     */
    public <I, O> PreprocessorDescriptor<I, O> preprocessorNode(
            Supplier<Preprocessor<I, O>> preprocessorSupplier
    ) {
        PreprocessorDescriptor<I, O> x = new PreprocessorDescriptor<>(
                preprocessorSupplier);
        preprocessorDescriptors.add(x);
        return x;
    }

    /**
     * Declares an {@link Engine}.
     *
     * @param engineSupplier the method that will create the behavior.
     * @return a descriptor of the declared actor, for chain call support.
     */
    public EngineDescriptor engineNode(Supplier<Engine> engineSupplier) {
        EngineDescriptor x = new EngineDescriptor(engineSupplier);
        engineDescriptors.add(x);
        return x;
    }

    /**
     * Declares a {@link Controller}.
     *
     * @param controllerSupplier the method that will create the behavior.
     * @return a descriptor of the declared actor, for chain call support.
     */
    public ControllerDescriptor controllerNode(
            Supplier<Controller> controllerSupplier
    ) {
        ControllerDescriptor x = new ControllerDescriptor(controllerSupplier);
        controllerDescriptors.add(x);
        return x;
    }

    /**
     * Declares a {@link DataSetManager}.
     *
     * @param dataSetManagerSupplier the method that will create the behavior.
     * @param <K>                    the type of the keys of the data instances
     * @param <V>                    the type of the data instances
     * @return a descriptor of the declared actor, for chain call support.
     */
    public <K, V> DataSetManagerDescriptor<K, V>
    dataSetManagerNode(Supplier<DataSetManager<K, V>> dataSetManagerSupplier) {
        DataSetManagerDescriptor<K, V> x
                = new DataSetManagerDescriptor<>(dataSetManagerSupplier);
        dataSetManagerDescriptors.add(x);
        return x;
    }

    /**
     * Declares a {@link Reporter}.
     *
     * @param reporterSupplier the method that will create the behavior.
     * @param <O>              the type of data accepted by the reporter
     * @return a descriptor of the declared actor, for chain call support.
     */
    public <O> ReporterDescriptor<O> reporterNode(
            Supplier<Reporter<O>> reporterSupplier
    ) {
        ReporterDescriptor<O> x = new ReporterDescriptor<>(reporterSupplier);
        reporterDescriptors.add(x);
        return x;
    }

    /**
     * Declares an {@link Acquirer}.
     *
     * @param name             the name assigned to the declared actor
     * @param acquirerSupplier the method that will create the behavior
     * @param <T>              the type of the acquired datQa
     * @return a descriptor of the declared actor, for chain call support.
     */
    public <T> AcquirerDescriptor<T> acquirerNode(
            String name,
            Supplier<Acquirer<T>> acquirerSupplier
    ) {
        AcquirerDescriptor<T> x = new AcquirerDescriptor<>(
                name,
                acquirerSupplier
        );
        acquirerDescriptors.add(x);
        return x;
    }

    /**
     * Declares a {@link Preprocessor}
     *
     * @param name                 the name assigned to the declared actor
     * @param preprocessorSupplier the method that will create the behavior.
     * @param <I>                  the input data type of the preprocessor
     * @param <O>                  the output data type of the preprocessor
     * @return a descriptor of the declared actor, for chain call support.
     */
    public <I, O> PreprocessorDescriptor<I, O> preprocessorNode(
            String name,
            Supplier<Preprocessor<I, O>> preprocessorSupplier
    ) {
        PreprocessorDescriptor<I, O> x = new PreprocessorDescriptor<>(
                name,
                preprocessorSupplier
        );
        preprocessorDescriptors.add(x);
        return x;
    }

    /**
     * Declares an {@link Engine}.
     *
     * @param name           the name assigned to the declared actor
     * @param engineSupplier the method that will create the behavior.
     * @return a descriptor of the declared actor, for chain call support.
     */
    public EngineDescriptor engineNode(
            String name,
            Supplier<Engine> engineSupplier
    ) {
        EngineDescriptor x = new EngineDescriptor(
                name,
                engineSupplier
        );
        engineDescriptors.add(x);
        return x;

    }

    /**
     * Declares a {@link Controller}.
     *
     * @param name               the name assigned to the declared actor
     * @param controllerSupplier the method that will create the behavior.
     * @return a descriptor of the declared actor, for chain call support.
     */
    public ControllerDescriptor controllerNode(
            String name,
            Supplier<Controller> controllerSupplier
    ) {
        ControllerDescriptor x = new ControllerDescriptor(
                name,
                controllerSupplier
        );
        controllerDescriptors.add(x);
        return x;

    }

    /**
     * Declares a {@link DataSetManager}.
     *
     * @param name                   the name assigned to the declared actor
     * @param dataSetManagerSupplier the method that will create the behavior.
     * @param <K>                    the type of the keys of the data instances
     * @param <V>                    the type of the data instances
     * @return a descriptor of the declared actor, for chain call support.
     */
    public <K, V> DataSetManagerDescriptor<K, V>
    dataSetManagerNode(
            String name,
            Supplier<DataSetManager<K, V>> dataSetManagerSupplier
    ) {
        DataSetManagerDescriptor<K, V> x = new DataSetManagerDescriptor<>(
                name,
                dataSetManagerSupplier
        );
        dataSetManagerDescriptors.add(x);
        return x;
    }

    /**
     * Declares a {@link Reporter}.
     *
     * @param name             the name assigned to the declared actor
     * @param reporterSupplier the method that will create the behavior.
     * @param <O>              the type of data accepted by the reporter
     * @return a descriptor of the declared actor, for chain call support.
     */
    public <O> ReporterDescriptor<O> reporterNode(
            String name,
            Supplier<Reporter<O>> reporterSupplier
    ) {
        ReporterDescriptor<O> x = new ReporterDescriptor<>(
                name,
                reporterSupplier
        );
        reporterDescriptors.add(x);
        return x;
    }


    /**
     * Method invoked internally by the {@link Master} at startup to create the
     * initial structure of the application.
     */
    public Map<Reference, NodeInterface> buildStructure(Master creator) {
        creator.actorLog("Building initial structure");

        Map<NodeDescriptor, NodeInterface> tmpInterfaces = new HashMap<>();


        for (NodeDescriptor descriptor : getAllDescriptors()) {
            tmpInterfaces.computeIfAbsent(
                    descriptor,
                    (d1) -> descriptor.createActor(creator)
            );
        }


        getAcquirerDescriptors().stream()
                .flatMap(ad -> ad.getDataLinks().stream())
                .forEach(dataLink -> connect(creator, tmpInterfaces, dataLink));

        getPreprocessorDescriptors().stream()
                .flatMap(pd -> pd.getDataLinks().stream())
                .forEach(dataLink -> connect(creator, tmpInterfaces, dataLink));


        for (ControllerDescriptor controllerDesc : getControllerDescriptors()) {
            NodeInterface controller = tmpInterfaces.get(controllerDesc);
            for (PreprocessorDescriptor<?, ?> linkedPreprocessor :
                    controllerDesc.getLinkedPreprocessors()) {
                connect(
                        creator,
                        tmpInterfaces.get(linkedPreprocessor),
                        controller
                );
            }

            for (AcquirerDescriptor<?> linkedAcquirer :
                    controllerDesc.getLinkedAcquirers()) {
                connect(creator, tmpInterfaces.get(linkedAcquirer), controller);
            }

            for (EngineDescriptor linkedEngine :
                    controllerDesc.getLinkedEngines()) {
                NodeInterface engine = tmpInterfaces.get(linkedEngine);
                if (engine != null) {
                    creator.send(
                            controller.getReference(),
                            new AssignEngine(engine.getReference())
                    );
                }
            }

            for (DataLink output : controllerDesc.getOutputs()) {
                connect(creator, tmpInterfaces, output);
            }

        }

        creator.actorLog("Building done, "
                + tmpInterfaces.size() + " nodes created");

        Map<Reference, NodeInterface> result = new HashMap<>();
        tmpInterfaces.forEach((__, value) -> result.put(
                value.getReference(),
                value
        ));
        return result;
    }

    private void connect(
            Behavior starter,
            Map<NodeDescriptor, NodeInterface> interfaces,
            DataLink dataLink
    ) {
        var producerReference = interfaces.get(dataLink.getProducer());
        var consumerReference = interfaces.get(dataLink.getConsumer());
        connect(starter, producerReference, consumerReference);
    }

    private void connect(
            Behavior starter,
            NodeInterface producer,
            NodeInterface consumer
    ) {
        if (producer != null && consumer != null) {
            starter.send(
                    consumer.getReference(),
                    new AssignDataSource(producer.getReference())
            );
        }
    }

    public Set<AcquirerDescriptor<?>> getAcquirerDescriptors() {
        return acquirerDescriptors;
    }

    public Set<PreprocessorDescriptor<?, ?>> getPreprocessorDescriptors() {
        return preprocessorDescriptors;
    }

    public Set<EngineDescriptor> getEngineDescriptors() {
        return engineDescriptors;
    }

    public Set<DataSetManagerDescriptor<?, ?>> getDataSetManagerDescriptors() {
        return dataSetManagerDescriptors;
    }

    public Set<ControllerDescriptor> getControllerDescriptors() {
        return controllerDescriptors;
    }

    public Set<ReporterDescriptor<?>> getReporterDescriptors() {
        return reporterDescriptors;
    }

    public Set<NodeDescriptor> getAllDescriptors() {
        return Sets.union(
                getAcquirerDescriptors(),
                getPreprocessorDescriptors(),
                getEngineDescriptors(),
                getDataSetManagerDescriptors(),
                getReporterDescriptors(),
                getControllerDescriptors()
        );
    }
}
