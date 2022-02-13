package it.unipr.sowide.util.promise;

import it.unipr.sowide.util.Mappable;
import it.unipr.sowide.util.Require;
import it.unipr.sowide.util.annotations.Immutable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A {@code Promise} that a computation will eventually be performed, and a
 * result of type {@code T} will be returned.
 * <p></p>
 * A Promise can be initialized by using a constructor. With the
 * inizialization of the promise, it is defined what has to be done in order
 * to let the computation start and how the promise will eventually
 * <i>resolved</i> or <i>rejected</i>.
 * After the initialization, a set of operations can be enqueued on the result
 * value (or on the error that caused the rejection) by means of various methods
 * in this class (e.g. {@link #then(Resolver)}, {{@link #onError(Rejecter)}}).
 * To effectively start the computation {@link #compel()} should be invoked.
 * After it is compelled, <i>promise</i> can be <i>resolved</i> or
 * <i>rejected</i>. Some promises can be in some degree of both states,
 * especially those that represent a collection of computations started in
 * parallel.
 *
 * @param <T> the type of the eventual result value of the promise
 * @param <E> the type of the eventual error-representing value of the promise
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
@Immutable
public class Promise<T, E>
        implements Mappable<T>,
        AutoCloseable {
    private Resolver<T> resolver = (t) -> {
    };
    private Rejecter<E> rejecter = (e) -> {
    };
    private final BiConsumer<Resolver<T>, Rejecter<E>> initialization;

    /**
     * Creates and initializes a new promise. The {@code initialize} method
     * should define how the computation will be started (e.g. a request is
     * sent) and how to signal that it is completed (by invoking
     * {@link Resolver#resolve(Object)} or {{@link Rejecter#reject(Object)}}).
     *
     * @param initialize the initialization method
     */
    public Promise(BiConsumer<Resolver<T>, Rejecter<E>> initialize) {
        Require.nonNull(initialize);
        this.initialization = initialize;
    }


    /**
     * Defines what to do right after the computation behind this promise is
     * resolved and a result of type {@link T} is available.
     *
     * @param then a function that accepts the result of type {@link T} when it
     *             is available.
     * @return a Promise that when compelled, executes the computation behind
     * this promise and the computation specified by {@code then}.
     */
    public Promise<T, E> then(Resolver<T> then) {
        Require.nonNull(then);
        Promise<T, E> result = new Promise<>(initialization);
        result.resolver = this.resolver.andThen(then);
        result.rejecter = this.rejecter;
        return result;
    }

    /**
     * Defines what to do right after the computation behind this promise is
     * rejected and an error value of type {@link E} is available.
     *
     * @param after a function that accepts the error of type {@link E} when it
     *              is available.
     * @return a Promise that when compelled, executes the computation behind
     * this promise and the computation specified by {@code after} if it is
     * rejected.
     */
    public Promise<T, E> onError(Rejecter<E> after) {
        Promise<T, E> result = new Promise<>(initialization);
        result.resolver = this.resolver;
        result.rejecter = this.rejecter.andThen(after);
        return result;
    }

    /**
     * Upon invocation of this method, the initial computation behind this
     * promise is started.
     */
    public void compel() {
        this.initialization.accept(resolver, rejecter);
    }

    /**
     * Defines what to do right after the computation behind this promise is
     * resolved and a result of type {@link T} is available; the function
     * provided as argument should return a new Promise, which is subsequently
     * compelled.
     *
     * @param function a function that accepts the result of type {@link T}
     *                 when it is available and returns a new promise to be
     *                 compelled when done.
     * @param <R>      the result type of the new promise
     * @return a Promise that when compelled, executes the computation behind
     * this promise, the computation inside by {@code thenAwait}, and the
     * computation behind the promise returned by {@code thenAwait}.
     */
    public <R> Promise<R, E> thenAwait(
            Function<? super T, ? extends Promise<R, E>> function
    ) {
        return flatMap(function);
    }

    /**
     * Defines how to transform the result right after the computation behind
     * this promise is resolved and a result of type {@link T} is available.
     *
     * @param function a function that accepts the result of type {@link T}
     *                 when it is available, and returns a value of type
     *                 {@link R} at the end.
     * @param <R>      the type of the transformed result
     * @return a Promise that when compelled, executes the computation behind
     * this promise and the computation specified by {@code function}; the
     * value returned by {@code function} is used as result value of the
     * returned promise.
     */
    @Override
    public <R> Promise<R, E> map(Function<T, R> function) {
        return new Promise<>((res, rej) -> {
            Promise.this
                    .then(t -> res.resolve(function.apply(t)))
                    .onError(rej)
                    .compel();
        });
    }

    /**
     * Defines how to transform the error right after the computation behind
     * this promise is rejected and an error of type {@link E} is available.
     *
     * @param function a function that accepts the error of type {@link E}
     *                 when it is available, and returns a value of type
     *                 {@link E} at the end.
     * @param <E2>     the type of the transformed error
     * @return a Promise that when compelled, executes the computation behind
     * this promise and, if an error occurred, it executes the computation
     * specified by {@code function}; the value returned by {@code function} is
     * used as error value of the returned promise.
     */
    public <E2> Promise<T, E2> mapError(
            Function<? super E, Optional<E2>> function
    ) {
        return new Promise<>((res, rej) -> {
            Promise.this
                    .then(res)
                    .onError((err) -> {
                        function.apply(err).ifPresent(rej::reject);
                    }).compel();
        });
    }

    /**
     * Equivalent to {@link #thenAwait(Function)}
     */
    public <R> Promise<R, E> flatMap(
            Function<? super T, ? extends Promise<R, E>> function
    ) {
        return new Promise<>((res, rej) -> {
            Promise.this
                    .then(t -> function.apply(t).then(res).compel())
                    .onError(rej)
                    .compel();
        });
    }

    /**
     * Transforms the promise into a promise that executes the computation
     * behind this promise and, when a result is available, it checks it against
     * the {@code predicate}. If the result does not match the predicate, the
     * returned promise will be rejected with the specified {@code error}.
     *
     * @param predicate the predicate to which the result will be checked against,
     *                  when it will be available.
     * @param error     the error that will be used as rejection value if the
     *                  result will not match the predicate
     * @return the transformed promise
     */
    public Promise<T, E> filter(Predicate<T> predicate, E error) {
        return new Promise<>((res, rej) -> {
            Promise.this
                    .then(t -> {
                        if (predicate.test(t)) {
                            res.resolve(t);
                        } else {
                            rej.reject(error);
                        }
                    })
                    .onError(rej)
                    .compel();
        });
    }


    /**
     * @return the computations that will be executed after a result is
     * available.
     */
    public Resolver<T> getResolver() {
        return resolver;
    }

    /**
     * @return the computations that will be executed after an error is signaled
     * as rejection value of this promise.
     */
    public Rejecter<E> getRejecter() {
        return rejecter;
    }

    /**
     * @return the method used to initialize this promise
     */
    public BiConsumer<Resolver<T>, Rejecter<E>> getInitialization() {
        return initialization;
    }

    /**
     * Equivalent to {@link #compel()}
     */
    @Override
    public void close() {
        this.compel();
    }
}
