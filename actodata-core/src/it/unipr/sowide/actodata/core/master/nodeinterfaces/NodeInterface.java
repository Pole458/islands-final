package it.unipr.sowide.actodata.core.master.nodeinterfaces;

import it.unipr.sowide.actodata.core.dataflow.ContainsReference;
import it.unipr.sowide.actodata.core.master.Master;
import it.unipr.sowide.util.Require;
import it.unipr.sowide.actodes.actor.Behavior;
import it.unipr.sowide.actodes.registry.Reference;

/**
 * An "interface" (i.e. an object with a reference and a set of common
 * interaction operations) that a {@link Master} uses to easily interact with
 * known actors of the ActoDatA application.
 * <p></p>
 * Please <b>do not</b> mistake this concept for a Java interface declared with
 * {@code interface}.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public abstract class NodeInterface implements ContainsReference {
    private final Master master;
    private final String name;
    private final Reference reference;
    private final Class<? extends Behavior> initialBehavior;

    /**
     * An interface for an actor with an assigned name.
     *
     * @param master          the master that uses the interface
     * @param name            the (privately known to the master) name assigned
     *                        to the actor
     * @param reference       the reference of the actor behind this interface
     * @param initialBehavior the known initial behavior of the actor
     */
    public NodeInterface(
            Master master,
            String name,
            Reference reference,
            Class<? extends Behavior> initialBehavior
    ) {
        Require.nonNull(master, name, reference, initialBehavior);
        this.master = master;
        this.name = name;
        this.reference = reference;
        this.initialBehavior = initialBehavior;
    }

    /**
     * An interface for an actor.
     * @param master          the master that uses the interface
     * @param reference       the reference of the actor behind this interface
     * @param initialBehavior the known initial behavior of the actor
     */
    public NodeInterface(
            Master master,
            Reference reference,
            Class<? extends Behavior> initialBehavior
    ) {
        Require.nonNull(master, reference, initialBehavior);
        this.master = master;
        this.name = null;
        this.reference = reference;
        this.initialBehavior = initialBehavior;
    }

    /** {@inheritDoc} **/
    @Override
    public Reference getReference() {
        return reference;
    }

    /**
     * @return the known initial behavior of the actor behind this interface
     */
    public Class<? extends Behavior> getInitialBehavior() {
        return initialBehavior;
    }

    /**
     * @return true if the master using this interface has assigned a private
     * name to the actor
     */
    public boolean isNamed() {
        return name != null;
    }

    /**
     * @return the (privately known to the master) name assigned to the actor
     */
    public String getName() {
        return name;
    }

    /**
     * @return an instance of the master using this interface, for delegation
     * to default methods only.
     */
    public Master master() {
        return master;
    }
}
