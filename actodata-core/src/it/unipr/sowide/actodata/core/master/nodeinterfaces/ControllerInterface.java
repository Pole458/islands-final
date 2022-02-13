package it.unipr.sowide.actodata.core.master.nodeinterfaces;

import it.unipr.sowide.actodata.core.master.Master;
import it.unipr.sowide.actodes.actor.Behavior;
import it.unipr.sowide.actodes.registry.Reference;

/**
 * A {@link NodeInterface} of an controller.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class ControllerInterface
        extends NodeInterface
        implements ReceiverInterface {
    /**
     * @see NodeInterface#NodeInterface(Master, Reference, Class)
     */
    public ControllerInterface(
            Master master,
            Reference reference,
            Class<? extends Behavior> initialBehavior
    ) {
        super(master, reference, initialBehavior);
    }

    /**
     * @see NodeInterface#NodeInterface(Master, String, Reference, Class)
     */
    public ControllerInterface(
            Master master,
            String name,
            Reference reference,
            Class<? extends Behavior> initialBehavior
    ) {
        super(master, name, reference, initialBehavior);
    }
}
