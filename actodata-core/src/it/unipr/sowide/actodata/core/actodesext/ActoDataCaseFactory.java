package it.unipr.sowide.actodata.core.actodesext;

import it.unipr.sowide.actodata.core.actodesext.content.Query;
import it.unipr.sowide.actodata.core.actodesext.content.QueryProperty;
import it.unipr.sowide.actodes.actor.*;
import it.unipr.sowide.actodes.interaction.Error;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static it.unipr.sowide.actodata.core.actodesext.IsInstancePatterns.isInstanceContentPattern;
import static it.unipr.sowide.util.CheckedCast.cast;

/**
 * An object used during initial configuration of an ActoDatA behavior to define
 * the message pattern - message handler pairs.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 * @see CaseFactory
 */
public class ActoDataCaseFactory implements IActoDataCaseFactory {
    private final ActoDataBaseBehavior behavior;
    private final CaseFactory delegate;


    /**
     * Creates a case factory that delegates to {@code delegate}.
     *
     * @param behavior the behavior being configured
     * @param delegate the delegate {@link CaseFactory}
     */
    public ActoDataCaseFactory(
            ActoDataBaseBehavior behavior,
            CaseFactory delegate
    ) {
        this.behavior = behavior;
        this.delegate = delegate;
    }


    /**
     * Creates a case factory that delegates to the {@link CaseFactory} of
     * {@code delegate}.
     *
     * @param behavior the behavior being configured
     * @param delegate the {@link ActoDataCaseFactory} from which the delegate
     *                 is taken from
     */
    public ActoDataCaseFactory(
            ActoDataBaseBehavior behavior,
            ActoDataCaseFactory delegate
    ) {
        this.behavior = behavior;
        this.delegate = delegate.delegate;
    }


    /**
     * {@inheritDoc}
     **/
    @Override
    public <T> void defineForContentOfType(
            Class<? extends T> type,
            BiFunction<T, Message, Behavior> h
    ) {
        MessageHandler messageHandler = m -> {
            Behavior result = null;
            if (type.isInstance(m.getContent())) {
                T casted = type.cast(m.getContent());
                result = h.apply(casted, m);
            } else {
                behavior.send(m, Error.UNEXPECTEDCONTENT);
            }
            return result;
        };
        define(isInstanceContentPattern(type), messageHandler);
    }


    /**
     * {@inheritDoc}
     **/
    @Override
    public <T> void onContentOfType(
            Class<? extends T> type,
            BiConsumer<T, Message> h
    ) {
        defineForContentOfType(type, (content, message) -> {
            h.accept(content, message);
            return null;
        });
    }


    /**
     * {@inheritDoc}
     **/
    @Override
    public <R> void serveProperty(
            String propertyName,
            Supplier<? extends R> getter
    ) {
        on(Query.createPattern(
                QueryProperty.class,
                QueryProperty.withName(propertyName)
        ), (message) -> {
            cast((Query<QueryProperty>) null, message.getContent())
                    .ifSuccessful((content) -> {
                        R result = getter.get();
                        if (result != null) {
                            behavior.send(message, result);
                        }
                    });
        });
    }


    /**
     * {@inheritDoc}
     **/
    @Override
    public void on(MessagePattern p, Consumer<Message> h) {
        define(p, m -> {
            h.accept(m);
            return null;
        });
    }


    /**
     * {@inheritDoc}
     **/
    @Override
    public void define(MessagePattern p, MessageHandler h) {
        delegate.define(p, (message) -> {
            behavior.resetChangeBehavior();
            var returnedBehavior = h.process(message);
            Behavior nextBehavior;
            if (returnedBehavior == null) {
                nextBehavior = behavior.nextBehavior;
            } else {
                nextBehavior = returnedBehavior;
            }
            if (nextBehavior instanceof Shutdown) {
                behavior.onShutdown();
            }
            return nextBehavior;
        });
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public ActoDataBaseBehavior self() {
        return behavior;
    }
}
