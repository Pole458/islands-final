package it.unipr.sowide.actodata.core.acquirer;

import it.unipr.sowide.actodata.core.acquirer.content.PauseAcquirer;
import it.unipr.sowide.actodata.core.acquirer.content.UnPauseAcquirer;
import it.unipr.sowide.actodata.core.actodesext.ActoDataBaseBehavior;
import it.unipr.sowide.actodata.core.actodesext.ActoDataCaseFactory;
import it.unipr.sowide.actodata.core.dataflow.Data;
import it.unipr.sowide.actodata.core.dataflow.PushSender;
import it.unipr.sowide.actodes.actor.Behavior;
import it.unipr.sowide.actodes.interaction.Inform;
import it.unipr.sowide.util.CheckedCast;
import it.unipr.sowide.actodes.interaction.Done;
import it.unipr.sowide.actodes.interaction.Error;
import it.unipr.sowide.actodes.registry.Reference;
import it.unipr.sowide.util.annotations.Open;

/**
 * A partial specialization of an <i>acquirer</i>. An acquirer continuously
 * extracts new data from a source and sends {@link Data} messages to other
 * nodes.
 * <p></p>
 * Note that an acquirer, when spawned, is by default <i>paused</i>.
 * To let it start sending data messages, it should {@link Acquirer#unpause()}
 * itself or another actor should send the {@link UnPauseAcquirer} message to
 * it.
 *
 * @param <T> the type of the data.
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public abstract class Acquirer<T> extends ActoDataBaseBehavior {

    private final PushSender toPreprocessors = new PushSender();

    private boolean paused = true;

    private final AcquiredDataAcceptor<T> acquiredDataAcceptor =
            new AcquiredDataAcceptor<>() {
                @Override
                void onDataEvent(NewData<T> data) {
                    send(Acquirer.this.getReference(), data);
                }
            };


    @Override
    public final void actoDataNodeCases(ActoDataCaseFactory c) {
        toPreprocessors.defineFragmentCases(this, c);

        c.onContentOfType(PauseAcquirer.class, (p, m) -> {
            pause();
            send(m, Done.DONE);
        });

        c.onContentOfType(UnPauseAcquirer.class, (p, m) -> {
            unpause();
            send(m, Done.DONE);
        });


        c.onContentOfType(NewData.class, (data, message) -> {
            CheckedCast.cast((T) null, data.getData())
                    .ifSuccessfulOrElse((tData) -> {
                        if (!paused) {

                            onSubmittingData(tData);
                            toPreprocessors.sendData(this, wrapData(tData));
                            onDataSubmitted(tData);

                        } else {
                            send(message, Error.REFUSEDREQUEST);
                        }
                    }, (/*else*/) -> {
                        send(message, Error.UNKNOWNCONTENT);
                    });
        });

        acquirerCases(c);
    }


    /**
     * This method should initialize the task of listening to new data from the
     * external data sources. The method internally calls
     * {@link AcquiredDataAcceptor#submit(Object)} on the provided
     * {@code acceptor} to submit the new data to the actors subscribed to this
     * acquirer.
     * <p></p>
     * This method is called right after the start of the acquirer.
     *
     * @param acceptor the acceptor for new data
     */
    public abstract void setupListening(AcquiredDataAcceptor<T> acceptor);

    /**
     * When the acquirer is paused, no new data is sent to the subscribed
     * actors. This method sets the <i>paused</i> flag.
     */
    public void pause() {
        this.paused = true;
    }

    /**
     * When the acquirer is paused, no new data is sent to the subscribed
     * actors. This method clears the <i>paused</i> flag.
     */
    public void unpause() {
        this.paused = false;
    }

    /**
     * When the acquirer is paused, no new data is sent to the subscribed
     * actors.
     *
     * @return the state of the <i>paused</i> flag.
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * An inner class that defines the acceptor of new data for
     * {@link #setupListening(AcquiredDataAcceptor)}.
     *
     * @param <T> the type of the data
     */
    public abstract static class AcquiredDataAcceptor<T> {
        /**
         * Method implmented by the {@link Acquirer} base class right before
         * starting {@link #setupListening(AcquiredDataAcceptor)}.
         */
        abstract void onDataEvent(NewData<T> data);

        /**
         * Use this method to submit new data to the subscribed actors.
         *
         * @param data the new data to submit
         */
        public void submit(T data) {
            onDataEvent(new NewData<>(data));
        }
    }

    /**
     * Method called right before new data is being submitted.
     *
     * @param data the data being submitted
     */
    @Open
    public void onSubmittingData(T data) {
        //override if needed
    }

    /**
     * Method called right after new data is being submitted.
     *
     * @param data the data being submitted
     */
    @Open
    public void onDataSubmitted(T data) {
        //override if needed
    }

    /**
     * Method that defines additional cases for this acquirer. By default, no
     * other cases are added than the required ones for basic acquirer
     * operation. Override this if needed.
     *
     * @param caseFactory the case factory
     */
    @Open
    public void acquirerCases(ActoDataCaseFactory caseFactory) {
        //override if needed
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    @Open
    public final void onStart() {
        setupListening(acquiredDataAcceptor);
    }

    /**
     * Specialization of {@link ActoDataBaseBehavior#changeBehavior(Behavior)}
     * used for acquirer instances. If this method is used to change the
     * behavior to another {@link Acquirer}, everything that represent the
     * internal state of a generic acquirer is copied into {@code b}. This
     * allows to keep structural continuity of the application (e.g. by copying
     * the list of subscribers).
     *
     * @param b the new acquirer behavior
     */
    protected void changeBehavior(Acquirer<T> b) {
        for (Reference subscriber : toPreprocessors.getSubscribers()) {
            b.toPreprocessors.addSubscriber(subscriber);
        }
        b.paused = paused;
        super.changeBehavior(b);
    }


    /**
     * A message sent internally from the acquirer to himself to notify the
     * presence of new data.
     *
     * @param <T> the type of the data
     * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
     */
    private static class NewData<T> implements Inform {
        private final T data;

        public NewData(T data) {
            this.data = data;
        }

        public T getData() {
            return data;
        }
    }
}
