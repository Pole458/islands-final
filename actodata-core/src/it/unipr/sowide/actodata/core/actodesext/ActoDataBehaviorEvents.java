package it.unipr.sowide.actodata.core.actodesext;

import it.unipr.sowide.actodata.core.master.MasterInterface;
import it.unipr.sowide.actodata.core.master.content.GlobalStart;
import it.unipr.sowide.actodes.actor.Behavior;
import it.unipr.sowide.actodes.executor.passive.CycleScheduler;

/**
 * Interface containing methods of a {@link ActoDataBaseBehavior} that
 * represents some common events of the actor. This interface is mainly meant
 * as a documentation aggregator.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public interface ActoDataBehaviorEvents {
    /**
     * Called right on start, when the behavior is started.
     */
    void onStart();

    /**
     * Called when the actor is coordinated by a {@link CycleScheduler} and a
     * {@link Behavior#CYCLE} message is received.
     */
    void onCycle();

    /**
     * Called right before shutdown by means of a
     * {@link ActoDataBehaviorActions#changeBehavior(Behavior)
     * changeBehavior(SHUTDOWN)} invocation.
     */
    void onShutdown();

    /**
     * Called when a {@link GlobalStart} message is received from the master
     * actor.
     *
     * @param masterInterface an object for a set of useful methods to build and
     *                        send common messages to the master.
     */
    void onGlobalStart(MasterInterface masterInterface);
}
