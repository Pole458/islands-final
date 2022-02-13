package it.unipr.sowide.actodata.core.master.nodeinterfaces;

import it.unipr.sowide.actodata.core.master.Master;
import it.unipr.sowide.actodes.actor.Behavior;
import it.unipr.sowide.actodes.registry.Reference;

/**
 * A {@link NodeInterface} of an acquirer.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class ReporterInterface
        extends NodeInterface
        implements ReceiverInterface {
    /**
     * @see NodeInterface#NodeInterface(Master, Reference, Class)
     */
    public ReporterInterface(
            Master master,
            Reference reference,
            Class<? extends Behavior> initialBehavior
    ) {
        super(master, reference, initialBehavior);
    }

    /**
     * @see NodeInterface#NodeInterface(Master, String, Reference, Class)
     */
    public ReporterInterface(
            Master master,
            String name,
            Reference reference,
            Class<? extends Behavior> initialBehavior
    ) {
        super(master, name, reference, initialBehavior);
    }

}
