package it.unipr.sowide.actodata.core.initialstructure;

import it.unipr.sowide.actodata.core.master.Master;
import it.unipr.sowide.actodata.core.master.nodeinterfaces.PreprocessorInterface;
import it.unipr.sowide.actodata.core.preprocessor.Preprocessor;
import it.unipr.sowide.actodes.registry.Reference;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * A descriptor for the declaration of a preprocessor, used by a
 * {@link Master} and with {@link ActoDataStructure} to build the preprocessor.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class PreprocessorDescriptor<I, O> implements NodeDescriptor {
    private final Supplier<Preprocessor<I, O>> preprocessorSupplier;
    private final Set<DataLink> dataLinks = new HashSet<>();
    private final String name;

    public PreprocessorDescriptor(
            Supplier<Preprocessor<I, O>> preprocessorSupplier
    ) {
        this.preprocessorSupplier = preprocessorSupplier;
        name = null;
    }

    public PreprocessorDescriptor(
            String name,
            Supplier<Preprocessor<I, O>> preprocessorSupplier
    ) {
        this.preprocessorSupplier = preprocessorSupplier;
        this.name = name;
    }

    public Set<DataLink> getDataLinks() {
        return dataLinks;
    }

    /** {@inheritDoc} **/
    @Override
    public PreprocessorInterface createActor(Master creator) {
        Preprocessor<I, O> behavior = preprocessorSupplier.get();
        Reference ref = creator.actor(behavior);
        if (isNamed()) {
            return new PreprocessorInterface(
                    creator,
                    name,
                    ref,
                    behavior.getClass()
            );
        } else {
            return new PreprocessorInterface(
                    creator,
                    ref,
                    behavior.getClass()
            );
        }
    }

    /** {@inheritDoc} **/
    @Override
    public boolean isNamed() {
        return name != null;
    }

    /** {@inheritDoc} **/
    @Override
    public String getName() {
        return name;
    }

    /**
     * Declares a data link from the preprocessor associated with this
     * descriptor and the preprocessor associated with {@code nextPreprocessor}.
     *
     * @param nextPreprocessor the other preprocessor descriptor
     * @param <O2>             the output type of the argument preprocessor
     * @return the argument descriptor itself, to create chained links in code.
     */
    public <O2> PreprocessorDescriptor<O, O2>
    link(PreprocessorDescriptor<O, O2> nextPreprocessor) {
        dataLinks.add(new DataLink(this, nextPreprocessor));
        return nextPreprocessor;
    }

    /**
     * Declares a data link from the preprocessor associated with this
     * descriptor and the reporter associated with {@code reporterDescriptor}.
     *
     * @param reporterDescriptor the reporter descriptor
     * @return the argument descriptor itself, to create chained links in code.
     */
    public ReporterDescriptor<O> link(
            ReporterDescriptor<O> reporterDescriptor
    ){
        dataLinks.add(new DataLink(this, reporterDescriptor));
        return reporterDescriptor;
    }
}
