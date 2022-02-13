package it.unipr.sowide.actodata.core.actodesext.promise;

import it.unipr.sowide.actodes.interaction.Done;
import it.unipr.sowide.actodes.interaction.Error;
import it.unipr.sowide.util.promise.Promise;
import it.unipr.sowide.util.promise.Rejecter;
import it.unipr.sowide.util.promise.Resolver;
import it.unipr.sowide.util.promise.Unit;

import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * A Promise for a result of type {@link T} and supporting error values of type
 * {@link java.lang.Error}.
 *
 * @param <T> the type of the result.
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 * @see Promise
 */
public class ActoPromise<T> extends Promise<T, Error> {

    /**
     * {@inheritDoc}
     **/
    public ActoPromise(BiConsumer<Resolver<T>, Rejecter<Error>> initialize) {
        super(initialize);
    }

    /**
     * Creates a {@link Promise} that wraps the provided promise
     *
     * @param promise the promise
     */
    public ActoPromise(Promise<T, Error> promise) {
        this((resolver, errorRejecter) -> {
            promise.then(resolver)
                    .onError(errorRejecter)
                    .compel();
        });
    }

    /**
     * Creates an {@link ActoPromise} that wraps the provided promise
     *
     * @param p   the promise
     * @param <T> the type of the result of the promise
     * @return the {@link ActoPromise}
     */
    public static <T> ActoPromise<T> actoPromise(Promise<T, Error> p) {
        return new ActoPromise<>(p);
    }

    /**
     * Converts a {@link Promise} that resolves with a {@link Unit} to an
     * {@link ActoPromise} that resolves with a {@link Done} message.
     *
     * @param p the promise
     * @return the ActoPromise
     */
    public static ActoPromise<Done> unitToDone(Promise<Unit, Error> p) {
        return actoPromise(p.map((__) -> Done.DONE));
    }

    /**
     * Converts a {@link Promise} that rejects with a {@link Throwable} to an
     * {@link ActoPromise} that rejects with a {@link Error#FAILEDEXECUTION}
     * message.
     *
     * @param p   the promise
     * @param <T> the type of the resolution value
     * @return the ActoPromise
     */
    public static <T> ActoPromise<T> throwableToFailed(
            Promise<T, Throwable> p
    ) {
        return actoPromise(p.mapError((__) ->
                Optional.of(Error.FAILEDEXECUTION)));
    }


}
