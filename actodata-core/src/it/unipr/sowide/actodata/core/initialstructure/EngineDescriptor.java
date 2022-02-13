package it.unipr.sowide.actodata.core.initialstructure;

import it.unipr.sowide.actodata.core.engine.Engine;
import it.unipr.sowide.actodata.core.master.Master;
import it.unipr.sowide.actodata.core.master.nodeinterfaces.EngineInterface;
import it.unipr.sowide.actodes.registry.Reference;

import java.util.function.Supplier;

/**
 * A descriptor for the declaration of an engine, used by a
 * {@link Master} and with {@link ActoDataStructure} to build the engine.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class EngineDescriptor implements NodeDescriptor {

    private final Supplier<Engine> engineSupplier;
    private final String name;


    public EngineDescriptor(Supplier<Engine> engineSupplier) {
        this.engineSupplier = engineSupplier;
        name = null;
    }

    public EngineDescriptor(String name, Supplier<Engine> engineSupplier) {
        this.engineSupplier = engineSupplier;
        this.name = name;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public EngineInterface createActor(Master creator) {
        Engine behavior = engineSupplier.get();
        Reference ref = creator.actor(behavior);
        if (isNamed()) {
            return new EngineInterface(creator, name, ref, behavior.getClass());
        } else {
            return new EngineInterface(creator, ref, behavior.getClass());
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
