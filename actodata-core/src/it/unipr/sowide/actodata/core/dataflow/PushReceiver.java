package it.unipr.sowide.actodata.core.dataflow;

import it.unipr.sowide.actodata.core.actodesext.ActoDataBaseBehavior;
import it.unipr.sowide.actodata.core.actodesext.BehaviorFragment;
import it.unipr.sowide.actodata.core.actodesext.ActoDataCaseFactory;
import it.unipr.sowide.actodata.core.dataflow.content.AssignDataSource;
import it.unipr.sowide.actodata.core.dataflow.content.SubscribeForDataPush;
import it.unipr.sowide.actodata.core.dataflow.content.UnsubscribeForDataPush;
import it.unipr.sowide.actodes.actor.Message;
import it.unipr.sowide.actodes.interaction.Done;
import it.unipr.sowide.actodes.registry.Reference;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * A {@link BehaviorFragment} used to define cases and state to handle the
 * reception of push messages with content of type data .
 *
 * @param <T> the type of the data instances represented by expected data
 *            messages
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class PushReceiver<T> implements BehaviorFragment {

    private final Class<? extends T> expectedDataType;
    private final Set<Reference> registeredSenders = new HashSet<>();
    private BiConsumer<T, Message> dataPushedHandler = null;

    /**
     * A new receiver that handles data messages with data of type T.
     * The data handler can be later set by means of
     * {@link #setOnDataHandler(BiConsumer)}.
     *
     * @param expectedDataType the class of the expected data type
     */
    public PushReceiver(Class<? extends T> expectedDataType) {
        this.expectedDataType = expectedDataType;
    }

    /**
     * A new receiver that handles data messages with data of type T by
     * means of the provided {@link BiConsumer}.
     *
     * @param expectedDataType  the class of the expected data type
     * @param dataPushedHandler the data message handler
     */
    public PushReceiver(
            Class<? extends T> expectedDataType,
            BiConsumer<T, Message> dataPushedHandler
    ) {
        this.expectedDataType = expectedDataType;
        this.dataPushedHandler = dataPushedHandler;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public void defineFragmentCases(ActoDataBaseBehavior self, ActoDataCaseFactory c) {
        c.onContentOfType(AssignDataSource.class, (content, message) -> {
            PushReceiver.this.subscribe(self, content.getSourceReference());
            self.send(message, Done.DONE);
        });

        c.onContentOfType(expectedDataType, (content, message) -> {
            if (dataPushedHandler != null) {
                dataPushedHandler.accept(content, message);
            }
        });

        c.serveProperty("registeredSenders", this::getRegisteredSenders);
    }

    /**
     * Use this method to subscribe to an actor which is executing a
     * {@link PushSender} fragment.
     *
     * @param self             the behavior that uses this fragment
     * @param subscribingActor the actor to be subscribed to for new data
     *                         messages
     */
    public void subscribe(
            ActoDataBaseBehavior self,
            Reference subscribingActor
    ) {
        self.promiseFuture(
                subscribingActor,
                new SubscribeForDataPush(self.getReference()),
                (Done) null
        )
                .then(done -> {
                    this.registeredSenders.add(subscribingActor);
                })
                .compel();
    }

    /**
     * Unsubscribes from all the actors to which this behavior is subscribed
     * to.
     *
     * @param self the behavior that is using this fragment
     */
    public void unsubscribeAll(ActoDataBaseBehavior self) {
        Set<Reference> toBeRemoved = new HashSet<>();
        for (Reference registeredSender : getRegisteredSenders()) {
            self.promiseFuture(
                    registeredSender,
                    new UnsubscribeForDataPush(self.getReference()),
                    (Done) null
            ).then(done -> {
                toBeRemoved.add(registeredSender);
            }).compel();
        }
        toBeRemoved.forEach(registeredSenders::remove);
    }

    /**
     * Sets the {@link BiConsumer} that handles incoming data messages.
     *
     * @param dataPushedHandler the handler
     */
    public void setOnDataHandler(BiConsumer<T, Message> dataPushedHandler) {
        this.dataPushedHandler = dataPushedHandler;
    }

    /**
     * @return all the actor to which this receiver is subscribed to.
     */
    public Set<Reference> getRegisteredSenders() {
        return registeredSenders;
    }

    /**
     * Adds the reference of an actor to the list of references of actors which
     * this receiver is subscribed to.
     * <p></p>
     * Note that this does not subscribe the receiver. Use
     * {@link #subscribe(ActoDataBaseBehavior, Reference)} for that.
     *
     * @param sender the reference
     */
    public void addRegisteredSender(Reference sender) {
        this.registeredSenders.add(sender);
    }

}
