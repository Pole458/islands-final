package it.unipr.sowide.actodata.core.acquirer;

import it.unipr.sowide.actodes.executor.passive.CycleScheduler;
import it.unipr.sowide.util.annotations.Open;

/**
 * Acquirer specifically designed with {@link CycleScheduler}s. At each cycle,
 * a new piece of data is submitted (by means of
 * {@link #next(AcquiredDataAcceptor)}) until {@link #hasNext()} returns false.
 *
 * @param <T> the type of data to be submitted
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public abstract class LoopingAcquirer<T> extends Acquirer<T> {

    private AcquiredDataAcceptor<T> acquiredDataAcceptor = null;

    private boolean doneCalled = false;


    /** {@inheritDoc} **/
    @Override
    public void setupListening(AcquiredDataAcceptor<T> acceptor) {
        this.acquiredDataAcceptor = acceptor;
        setup();
    }


    /** {@inheritDoc} **/
    @Override
    public final void onCycle() {
        super.onCycle();
        if (hasNext()) {
            next(acquiredDataAcceptor);

        } else {
            if (!doneCalled) {
                onDoneAcquiring();
                doneCalled = true;
            }
        }
    }

    /**
     * Method that specifies what the acquirer does before the first cycle.
     */
    public abstract void setup();

    /**
     * Method that defines what and how data is sumbitted at each cycle.
     *
     * @param acquiredDataAcceptor the data acceptor.
     */
    public abstract void next(AcquiredDataAcceptor<T> acquiredDataAcceptor);

    /**
     * @return true if there is new data to be submitted or false if the
     * acquiring loop should stop.
     */
    public abstract boolean hasNext();

    /**
     * Method called right after {@link #hasNext()} returned false and the loop
     * has ended.
     */
    @Open
    public void onDoneAcquiring() {
        // override if needed
    }
}
