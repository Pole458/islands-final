package it.unipr.sowide.actodata.core.initialstructure;

import it.unipr.sowide.actodata.core.controller.Controller;
import it.unipr.sowide.actodata.core.master.Master;
import it.unipr.sowide.actodata.core.master.nodeinterfaces.ControllerInterface;
import it.unipr.sowide.actodes.registry.Reference;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * A descriptor for the declaration of a controller, used by a
 * {@link Master} and with {@link ActoDataStructure} to build the controller.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class ControllerDescriptor implements NodeDescriptor {

    private final Supplier<Controller> controllerSupplier;
    private final Set<EngineDescriptor> linkedEngines
            = new HashSet<>();
    private final Set<PreprocessorDescriptor<?, ?>> linkedPreprocessors
            = new HashSet<>();
    private final Set<AcquirerDescriptor<?>> linkedAcquirers
            = new HashSet<>();
    private final Set<DataLink> outputs
            = new HashSet<>();
    private final String name;

    public ControllerDescriptor(Supplier<Controller> controllerSupplier) {
        this.controllerSupplier = controllerSupplier;
        name = null;
    }

    public ControllerDescriptor(
            String name,
            Supplier<Controller> controllerSupplier
    ) {
        this.controllerSupplier = controllerSupplier;
        this.name = name;
    }

    /**
     * Declares that the controller should control the engine whose declaration
     * is represented by the provided {@code engine}.
     *
     * @param engine the engine.
     */
    public void linkEngine(EngineDescriptor engine) {
        linkedEngines.add(engine);
    }

    /**
     * Declares that the controller accepts in input data messages from the
     * preprocessor whose declaration is represented by the provided
     * {@code preprocessor}.
     *
     * @param preprocessor the preprocessor
     */
    public void linkInput(PreprocessorDescriptor<?, ?> preprocessor) {
        linkedPreprocessors.add(preprocessor);
    }

    /**
     * Declares that the controller accepts in input data messages from the
     * acquirer whose declaration is represented by the provided
     * {@code acquirer}.
     *
     * @param acquirer the acquirer
     */
    public void linkInput(AcquirerDescriptor<?> acquirer) {
        linkedAcquirers.add(acquirer);
    }

    /**
     * Declares that the controller emits data messages to the
     * preprocessor whose declaration is represented by the provided
     * {@code preprocessorDescriptor}.
     *
     * @param preprocessorDescriptor the preprocessor
     * @param <O>                    the output type of the preprocessor
     * @return the argument descriptor itself, to create chained links in code.
     */
    public <O> PreprocessorDescriptor<?, O> linkOutput(
            PreprocessorDescriptor<?, O> preprocessorDescriptor
    ) {
        outputs.add(new DataLink(this, preprocessorDescriptor));
        return preprocessorDescriptor;
    }

    /**
     * Declares that the controller emits data messages to the
     * reporter whose declaration is represented by the provided
     * {@code reporterDescriptor}.
     *
     * @param reporterDescriptor the reporter
     * @param <O>                the output type of the reporter
     * @return the argument descriptor itself, to create chained links in code.
     */
    public <O> ReporterDescriptor<O> linkOutput(
            ReporterDescriptor<O> reporterDescriptor
    ) {
        outputs.add(new DataLink(this, reporterDescriptor));
        return reporterDescriptor;
    }


    public Set<PreprocessorDescriptor<?, ?>> getLinkedPreprocessors() {
        return linkedPreprocessors;
    }

    public Set<AcquirerDescriptor<?>> getLinkedAcquirers() {
        return linkedAcquirers;
    }

    public Set<EngineDescriptor> getLinkedEngines() {
        return linkedEngines;
    }

    public Set<DataLink> getOutputs() {
        return outputs;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public ControllerInterface createActor(Master creator) {
        Controller behavior = controllerSupplier.get();
        Reference ref = creator.actor(behavior);
        if (isNamed()) {
            return new ControllerInterface(creator, name, ref, behavior.getClass());
        } else {
            return new ControllerInterface(creator, ref, behavior.getClass());
        }
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public boolean isNamed() {
        return name != null;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public String getName() {
        return name;
    }
}
