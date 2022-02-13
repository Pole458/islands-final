package it.unipr.sowide.actodata.core.dataflow;


import it.unipr.sowide.actodata.core.actodesext.ActoDataBaseBehavior;
import it.unipr.sowide.actodata.core.actodesext.promise.ActoPromise;
import it.unipr.sowide.actodata.core.actodesext.promise.ActoPromises;
import it.unipr.sowide.actodes.actor.Behavior;
import it.unipr.sowide.actodes.actor.MessagePattern;
import it.unipr.sowide.actodes.filtering.MessagePatternField;
import it.unipr.sowide.actodes.filtering.constraint.UnaryConstraint;
import it.unipr.sowide.actodes.registry.Reference;
import it.unipr.sowide.util.annotations.Mutable;
import it.unipr.sowide.util.annotations.Open;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A set of references.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
@Open
@Mutable
public class ReferenceSet {
    private final Set<Reference> references = new HashSet<>();

    /**
     * Adds the reference to the set
     *
     * @param reference the reference
     * @return true if this set did not already contain the specified reference
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean addReference(Reference reference) {
        return this.references.add(reference);
    }

    /**
     * Removes the reference from the set
     *
     * @param reference the reference
     * @return true if this set contained the specified element
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean removeReference(Reference reference) {
        return this.references.remove(reference);
    }

    /**
     * Returns the references in a {@link Set}.
     *
     * @return the references
     */
    public Set<Reference> getReferences() {
        return references;
    }

    /**
     * Sends a message to all the references.
     *
     * @param self   the behavior of the actor sending the message
     * @param object the content of the message
     */
    public void sendToAll(Behavior self, Object object) {
        for (Reference reference : references) {
            self.send(reference, object);
        }
    }

    /**
     * Sends a message to all the references, then awaits a reply from everyone.
     * The returned promise is resolved with all the responses (of type
     * {@link T}) only when everyone replied.
     *
     * @param self      the behavior of the actor performing the requests
     * @param toSend    the content of the messages to send
     * @param typedNull just a placeholder parameter to help with type
     *                  inferring, a casted null ({@code (T) null}) will do.
     * @param <T>       the type of the expected responses
     * @return a promise that resolves with a list of all the responses
     * @see it.unipr.sowide.util.promise.Promises#all(Collection)
     */
    public <T> ActoPromise<List<T>> promiseFutureAll(
            ActoDataBaseBehavior self,
            Object toSend,
            T typedNull
    ) {
        return ActoPromises.all(references.stream()
                .map(ref -> self.promiseFuture(ref, toSend, typedNull))
                .collect(Collectors.toList())
        );
    }

    /**
     * Creates a {@link MessagePattern} that matches with any message sent from
     * any of the references in this set.
     *
     * @return the message pattern
     */
    public MessagePattern generateMessagePattern() {
        return new MessagePattern(
                new MessagePatternField(
                        MessagePattern.MessageField.SENDER,
                        (UnaryConstraint<Reference>) references::contains
                )
        );
    }

}
