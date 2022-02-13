package it.unipr.sowide.actodata.core.reporter;

import it.unipr.sowide.actodata.core.actodesext.ActoDataBaseBehavior;
import it.unipr.sowide.actodata.core.actodesext.ActoDataCaseFactory;
import it.unipr.sowide.actodata.core.dataflow.PushReceiver;
import it.unipr.sowide.actodata.core.dataflow.Data;
import it.unipr.sowide.actodes.actor.Behavior;
import it.unipr.sowide.actodes.registry.Reference;
import it.unipr.sowide.util.annotations.Open;

/**
 * A partial specialization of a <i>reporter</i>. A reporter receives data
 * messages from its input pipelines and performs some stateful final operation
 * on them (e.g. System I/O, exposing a web service)
 *
 * @param <I> the type of the data.
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public abstract class Reporter<I> extends ActoDataBaseBehavior {

    @SuppressWarnings({"unchecked", "RedundantCast", "rawtypes"})
    private final PushReceiver<Data<I>> receiver = new PushReceiver<>(
            (Class<? extends Data<I>>) (Class<? extends Data>) Data.class,
            (iData, message) -> {
                iData.getData(this)
                        .then(this::reportData)
                        .compel();
            }
    );

    /**
     * {@inheritDoc}
     **/
    @Override
    public void actoDataNodeCases(ActoDataCaseFactory c) {
        receiver.defineFragmentCases(this, c);
    }

    /**
     * Performs, on the input data, the operations specific to this reporter.
     *
     * @param iData the input data
     */
    public abstract void reportData(I iData);

    /**
     * Specialization of {@link ActoDataBaseBehavior#changeBehavior(Behavior)}
     * used for reporter instances. If this method is used to change the
     * behavior to another {@link Reporter}, everything that represent the
     * internal state of a generic reporter is copied into {@code b}. This
     * allows to keep structural continuity of the application (e.g. by copying
     * the list of subscribers).
     *
     * @param b the new reporter behavior
     */
    @Open
    protected void changeBehavior(Reporter<I> b) {
        for (Reference registeredSender : this.receiver.getRegisteredSenders()) {
            b.receiver.addRegisteredSender(registeredSender);
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
        receiver.unsubscribeAll(this);
    }
}
