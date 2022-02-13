package it.unipr.sowide.actodata.core.initialstructure;

import it.unipr.sowide.actodata.core.master.Master;
import it.unipr.sowide.actodata.core.master.nodeinterfaces.NodeInterface;
import it.unipr.sowide.actodata.core.master.nodeinterfaces.ReporterInterface;
import it.unipr.sowide.actodata.core.reporter.Reporter;
import it.unipr.sowide.actodes.registry.Reference;

import java.util.function.Supplier;

/**
 * A descriptor for the declaration of a reporter, used by a
 * {@link Master} and with {@link ActoDataStructure} to build the reporter.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class ReporterDescriptor<O> implements NodeDescriptor {
    private final Supplier<Reporter<O>> reporterSupplier;
    private final String name;

    public ReporterDescriptor(
            String name,
            Supplier<Reporter<O>> reporterSupplier
    ) {
        this.name = name;
        this.reporterSupplier = reporterSupplier;
    }

    public ReporterDescriptor(Supplier<Reporter<O>> reporterSupplier) {
        this.reporterSupplier = reporterSupplier;
        this.name = null;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public NodeInterface createActor(Master creator) {
        Reporter<O> behavior = reporterSupplier.get();
        Reference ref = creator.actor(behavior);
        if (isNamed()) {
            return new ReporterInterface(
                    creator,
                    name,
                    ref,
                    behavior.getClass()
            );
        } else {
            return new ReporterInterface(creator, ref, behavior.getClass());
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
