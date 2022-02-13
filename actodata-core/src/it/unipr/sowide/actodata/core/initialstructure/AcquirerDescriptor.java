package it.unipr.sowide.actodata.core.initialstructure;

import it.unipr.sowide.actodata.core.acquirer.Acquirer;
import it.unipr.sowide.actodata.core.master.Master;
import it.unipr.sowide.actodata.core.master.nodeinterfaces.AcquirerInterface;
import it.unipr.sowide.actodes.registry.Reference;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * A descriptor for the declaration of an acquirer, used by a
 * {@link Master} and with {@link ActoDataStructure} to build the acquirer.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class AcquirerDescriptor<T> implements NodeDescriptor {
    private final Supplier<Acquirer<T>> acquirerSupplier;
    private final Set<DataLink> dataLinks = new HashSet<>();
    private final String name;

    public AcquirerDescriptor(
            String name,
            Supplier<Acquirer<T>> acquirerSupplier
    ) {
        this.name = name;
        this.acquirerSupplier = acquirerSupplier;
    }

    public AcquirerDescriptor(Supplier<Acquirer<T>> acquirerSupplier) {
        this.acquirerSupplier = acquirerSupplier;
        this.name = null;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public AcquirerInterface createActor(Master creator) {
        Acquirer<T> behavior = acquirerSupplier.get();
        Reference ref = creator.actor(behavior);
        if (isNamed()) {
            return new AcquirerInterface(
                    creator,
                    name,
                    ref,
                    behavior.getClass()
            );
        } else {
            return new AcquirerInterface(
                    creator,
                    ref,
                    behavior.getClass()
            );
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

    /**
     * Declares a data link from the acquirer associated with this descriptor
     * and the preprocessor associated with {@code preprocessorDescriptor}.
     *
     * @param preprocessorDescriptor the preprocessor descriptor
     * @param <O>                    the output type of the preprocessor
     * @return the argument descriptor itself, to create chained links in code.
     */
    public <O> PreprocessorDescriptor<T, O> link(
            PreprocessorDescriptor<T, O> preprocessorDescriptor
    ) {
        dataLinks.add(new DataLink(this, preprocessorDescriptor));
        return preprocessorDescriptor;
    }

    /**
     * Declares a data link from the acquirer associated with this descriptor
     * and the preprocessor associated with {@code reporterDescriptor}.
     *
     * @param reporterDescriptor the preprocessor descriptor
     * @return the argument descriptor itself, to create chained links in code.
     */
    public ReporterDescriptor<T> link(
            ReporterDescriptor<T> reporterDescriptor
    ) {
        dataLinks.add(new DataLink(this, reporterDescriptor));
        return reporterDescriptor;
    }

    public Set<DataLink> getDataLinks() {
        return dataLinks;
    }
}
