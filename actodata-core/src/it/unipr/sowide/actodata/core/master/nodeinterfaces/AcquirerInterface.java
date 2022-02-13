package it.unipr.sowide.actodata.core.master.nodeinterfaces;

import it.unipr.sowide.actodata.core.acquirer.content.PauseAcquirer;
import it.unipr.sowide.actodata.core.acquirer.content.UnPauseAcquirer;
import it.unipr.sowide.actodata.core.master.Master;
import it.unipr.sowide.actodes.actor.Behavior;
import it.unipr.sowide.actodes.registry.Reference;

/**
 * A {@link NodeInterface} of an acquirer.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class AcquirerInterface
        extends NodeInterface
        implements SenderInterface {

    /**
     * @see NodeInterface#NodeInterface(Master, Reference, Class)
     */
    public AcquirerInterface(
            Master master,
            Reference reference,
            Class<? extends Behavior> initialBehavior
    ) {
        super(master, reference, initialBehavior);
    }

    /**
     * @see NodeInterface#NodeInterface(Master, String, Reference, Class)
     */
    public AcquirerInterface(
            Master master,
            String name,
            Reference reference,
            Class<? extends Behavior> initialBehavior
    ) {
        super(master, name, reference, initialBehavior);
    }

    /**
     * Asks the acquirer to unpause.
     */
    public void unpause() {
        master().send(getReference(), UnPauseAcquirer.INSTANCE);
    }

    /**
     * Asks the acquirer to pause.
     */
    public void pause() {
        master().send(getReference(), PauseAcquirer.INSTANCE);
    }
}
