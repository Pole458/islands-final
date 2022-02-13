package it.unipr.sowide.actodata.core.master.nodeinterfaces;

import it.unipr.sowide.actodata.core.master.Master;
import it.unipr.sowide.actodes.actor.Behavior;
import it.unipr.sowide.actodes.registry.Reference;

/**
 * A {@link NodeInterface} of a dataset manager.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class DataSetManagerInterface extends NodeInterface {
    /**
     * @see NodeInterface#NodeInterface(Master, Reference, Class)
     */
    public DataSetManagerInterface(
            Master master,
            Reference reference,
            Class<? extends Behavior> initialBehavior
    ) {
        super(master, reference, initialBehavior);
    }

    /**
     * @see NodeInterface#NodeInterface(Master, String, Reference, Class)
     */
    public DataSetManagerInterface(
            Master master,
            String name,
            Reference reference,
            Class<? extends Behavior> initialBehavior
    ) {
        super(master, name, reference, initialBehavior);
    }
}
