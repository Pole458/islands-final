package it.unipr.sowide.actodata.core.dataflow;

import it.unipr.sowide.actodata.core.actodesext.ActoDataBaseBehavior;
import it.unipr.sowide.actodata.core.actodesext.BehaviorFragment;
import it.unipr.sowide.actodata.core.actodesext.ActoDataCaseFactory;
import it.unipr.sowide.actodes.interaction.Done;
import it.unipr.sowide.actodes.registry.Reference;

import java.util.Set;

/**
 * A {@link BehaviorFragment} that encapsulates a set of references used to
 * identify the subscribers for an ad-hoc service provided by the behavior of
 * the actor.
 */
public class SubscriberSetFragment extends ReferenceSet implements BehaviorFragment {

    private final Class<? extends ContainsReference> subscribeMessageType;
    private final Class<? extends ContainsReference> unsubscribeMessageType;

    /**
     * A new {@link SubscriberSetFragment}.
     *
     * @param subscribeMessageType   the type of the message used to request to
     *                               the actor to subscribe a particular actor.
     * @param unsubscribeMessageType the type of the message used to request to
     *                               the actor to unsubscribe a particular
     *                               actor.
     */
    public SubscriberSetFragment(
            Class<? extends ContainsReference> subscribeMessageType,
            Class<? extends ContainsReference> unsubscribeMessageType
    ) {
        this.subscribeMessageType = subscribeMessageType;
        this.unsubscribeMessageType = unsubscribeMessageType;
    }

    /**
     * A new {@link SubscriberSetFragment}. Other actors cannot request to
     * unsubscribe via a message.
     *
     * @param subscribeMessageType   the type of the message used to request to
     *                               the actor to subscribe a particular actor.
     */
    public SubscriberSetFragment(
            Class<? extends ContainsReference> subscribeMessageType
    ) {
        this.subscribeMessageType = subscribeMessageType;
        this.unsubscribeMessageType = null;
    }

    /** {@inheritDoc} **/
    @Override
    public void defineFragmentCases(
            ActoDataBaseBehavior self,
            ActoDataCaseFactory c
    ) {
        c.onContentOfType(subscribeMessageType, (containsReference, message) -> {
            addReference(containsReference.getReference());
            self.send(message, Done.DONE);
        });

        if (unsubscribeMessageType != null) {
            c.onContentOfType(unsubscribeMessageType, (containsReference, message) -> {
                removeReference(containsReference.getReference());
                self.send(message, Done.DONE);
            });
        }
    }

    /**
     * Returns the references of the subscribers as a {@link Set}.
     * @return the references
     */
    public Set<Reference> getSubscribers() {
        return getReferences();
    }

    /**
     * Adds a subscriber reference.
     * @param reference the subscriber.
     */
    public void addSubscriber(Reference reference) {
        this.addReference(reference);
    }


}
