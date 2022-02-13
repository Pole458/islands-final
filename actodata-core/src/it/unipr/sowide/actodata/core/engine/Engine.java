package it.unipr.sowide.actodata.core.engine;

import it.unipr.sowide.actodata.core.actodesext.ActoDataBaseBehavior;
import it.unipr.sowide.actodata.core.actodesext.ActoDataCaseFactory;
import it.unipr.sowide.actodata.core.controller.Controller;
import it.unipr.sowide.actodata.core.controller.content.SubscribeEngineController;
import it.unipr.sowide.actodata.core.dataflow.SubscriberSetFragment;
import it.unipr.sowide.actodes.actor.Behavior;
import it.unipr.sowide.actodes.interaction.Inform;
import it.unipr.sowide.actodes.registry.Reference;
import it.unipr.sowide.util.annotations.Open;

import java.util.Set;

/**
 * A partial specialization of a behavior used to carry specific data analisys
 * tasks, like training, classification, regression, updating and others.
 * <p></p>
 * An engine is designed to be used as a service provider, mainly by the
 * {@link Controller}s which coordinates their actions, but can responds to
 * messages of other actors as well.
 * <p></p>
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 * @implSpec Classes extending this class should be designed by using components
 * defined as implementations of {@link EngineComponent}.
 */
public abstract class Engine extends ActoDataBaseBehavior {

    private final SubscriberSetFragment toBeNotified =
            new SubscriberSetFragment(SubscribeEngineController.class);

    @Override
    public final void actoDataNodeCases(ActoDataCaseFactory c) {
        components().forEach(comp -> comp.defineFragmentCases(this, c));
        engineCases(c);
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public final void onStart() {
        super.onStart();
        setup();
        sendMessageToControllers(EngineReady.INSTANCE);
    }

    /**
     * Defines specific cases for this engine behavior
     *
     * @param c the case factory
     */
    @Open
    protected void engineCases(ActoDataCaseFactory c) {
        // override if needed
    }

    /**
     * Called at startup, before any other operation of the engine
     */
    protected abstract void setup();

    /**
     * @return the set of components of this engine.
     */
    public abstract Set<EngineComponent> components();

    /**
     * Sends an {@link Inform} message to all the controllers controlling this
     * engine.
     * @param i the message to be sent
     */
    protected void sendMessageToControllers(Inform i) {
        toBeNotified.sendToAll(this, i);
    }

    /**
     * Specialization of {@link ActoDataBaseBehavior#changeBehavior(Behavior)}
     * used for engine instances. If this method is used to change the
     * behavior to another {@link Engine}, everything that represent the
     * internal state of a generic engine is copied into {@code b}. This
     * allows to keep structural continuity of the application (e.g. by keeping
     * by copying the list of subscribers).
     *
     * @param b the new engine behavior
     */
    protected void changeBehavior(Engine b) {
        for (Reference subscriber : toBeNotified.getSubscribers()) {
            b.toBeNotified.addSubscriber(subscriber);
        }
        super.changeBehavior(b);
    }

}
