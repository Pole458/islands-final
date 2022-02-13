package it.unipr.sowide.actodata.core.initialstructure;

import it.unipr.sowide.actodata.core.master.Master;
import it.unipr.sowide.actodata.core.master.nodeinterfaces.NodeInterface;

/**
 * A descriptor for the declaration of an ActoDatA actor not yet created. It
 * is used to define the components of an ActoDatA application's initial
 * structure by means of {@link ActoDataStructure}.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public interface NodeDescriptor {
    /**
     * Creates the declared actor.
     *
     * @param creator the behavior of the actor used to create the declared
     *                actor
     * @return a {@link NodeInterface} with useful methods to send common
     * messages and requests to the specified actor.
     */
    NodeInterface createActor(Master creator);

    /**
     * @return true if the declaration of this actor has been associated with
     * a name (the name is private to the master).
     */
    boolean isNamed();

    /**
     * The associated name to the declaration of this actor a name (the name is
     * private to the master).
     *
     * @return the name
     */
    String getName();
}
