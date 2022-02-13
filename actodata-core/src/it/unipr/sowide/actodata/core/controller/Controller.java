package it.unipr.sowide.actodata.core.controller;

import it.unipr.sowide.actodata.core.actodesext.ActoDataBaseBehavior;
import it.unipr.sowide.actodata.core.actodesext.ActoDataCaseFactory;
import it.unipr.sowide.actodata.core.controller.content.AssignEngine;
import it.unipr.sowide.actodata.core.controller.content.SubscribeEngineController;
import it.unipr.sowide.actodata.core.controller.extensions.ControllerExtension;
import it.unipr.sowide.actodata.core.dataflow.Data;
import it.unipr.sowide.actodata.core.dataflow.PushSender;
import it.unipr.sowide.actodata.core.dataflow.ReferenceSet;
import it.unipr.sowide.actodata.core.engine.Engine;
import it.unipr.sowide.actodes.actor.Behavior;
import it.unipr.sowide.actodes.actor.Message;
import it.unipr.sowide.actodes.interaction.Done;
import it.unipr.sowide.actodes.registry.Reference;
import it.unipr.sowide.util.annotations.Open;

/**
 * A Controller coordinates and defines the main logic of a distributed ActoDatA
 * task. Can receive and send {@link Data} messages from/to actor pipelines, and
 * coordinates the classification and machine learning tasks of a set of
 * controlled {@link Engine}s.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public abstract class Controller
        extends ActoDataBaseBehavior
        implements ControllerExtension, ControllerActions {

    private final ReferenceSet controlledEngines = new ReferenceSet();
    private final ControllerInputDataDispatcher inputDataReceiver
            = new ControllerInputDataDispatcher(this);

    private final PushSender output = new PushSender();


    /**
     * {@inheritDoc}
     **/
    @Override
    public final void actoDataNodeCases(ActoDataCaseFactory c) {
        c.onContentOfType(AssignEngine.class, (assignEngine, message) -> {
            controlledEngines.addReference(assignEngine.getReference());
            send(assignEngine.getReference(), new SubscribeEngineController(
                    this.getReference()));
            send(message, Done.DONE);
        });

        c.define(controlledEngines.generateMessagePattern(), (message) -> {
            this.onEngineMessage(message);
            return null;
        });

        inputDataReceiver.defineFragmentCases(this, c);
        output.defineFragmentCases(this, c);

        controllerCases(new ControllerCaseFactory(this, c, inputDataReceiver));
    }

    /**
     * {@inheritDoc}
     **/
    @Open
    @Override
    public final void onStart() {
        super.onStart();
        setup();
    }

    /**
     * Defines the actor <i>cases</i> (message pattern/handler pairs) for this
     * behavior using the provided {@link ControllerCaseFactory}.
     *
     * @param d the case factory
     */
    public abstract void controllerCases(ControllerCaseFactory d);

    /**
     * {@inheritDoc}
     **/
    @Override
    public void sendOutput(Object data) {
        output.sendData(this, wrapData(data));
    }

    /**
     * Method that defines what the actor does before starting to process data
     * messages.
     */
    @Open
    public void setup() {
        // override if needed
    }

    /**
     * Method that defines what the actor does upon reception of a message from
     * an engine.
     *
     * @param message the received message.
     */
    @Open
    public void onEngineMessage(Message message) {
        // override if needed
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public ReferenceSet getControlledEngines() {
        return controlledEngines;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public Controller getController() {
        return this;
    }

    /**
     * Specialization of {@link ActoDataBaseBehavior#changeBehavior(Behavior)}
     * used for controller instances. If this method is used to change the
     * behavior to another {@link Controller}, everything that represent the
     * internal state of a generic controller is copied into {@code b}. This
     * allows to keep structural continuity of the application (e.g. by copying
     * the list of subscribers).
     *
     * @param b the new acquirer behavior
     */
    protected void changeBehavior(Controller b) {
        for (Reference reference : controlledEngines.getReferences()) {
            b.controlledEngines.addReference(reference);
        }
        for (Reference reference : inputDataReceiver.getRegisteredSenders()) {
            b.inputDataReceiver.addRegisteredSender(reference);
        }
        super.changeBehavior(b);
    }

    /**
     * {@inheritDoc}
     **/
    @Open
    @Override
    public void onShutdown() {
        super.onShutdown();
        inputDataReceiver.unsubscribeAll(this);
    }
}
