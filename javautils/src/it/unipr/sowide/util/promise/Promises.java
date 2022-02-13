package it.unipr.sowide.util.promise;

import it.unipr.sowide.util.annotations.Namespace;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.*;
import java.util.stream.Collectors;

/**
 * A set of utility static methods used to build particular kind of
 * {@link Promises}.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
@Namespace
public class Promises {
    private Promises() {
    } // do not instantiate

    /**
     * Returns the composition of an empty {@link Unit} promise and the provided
     * promise.
     *
     * @param promise the promise to be compelled right after compelling the
     *                returned promise
     * @return the composition of an empty {@link Unit} promise and the provided
     * * promise.
     */
    public static Promise<Unit, Throwable> Do(
            Promise<Unit, Throwable> promise
    ) {
        return Do().thenAwait((__) -> promise);
    }

    /**
     * Returns a new emtpy {@link Unit} promise.
     *
     * @return a new emtpy {@link Unit} promise.
     */
    public static Promise<Unit, Throwable> Do() {
        return immediatelyResolve(Unit.UNIT);
    }

    /**
     * Returns a new promise which is resolved with {@code value} right after
     * it is compelled.
     *
     * @param value the value to be used as result value.
     * @param <T>   the type of {@code value}
     * @param <E>   the type of the error of this kind of promise
     * @return a new promise which is resolved with {@code value} right after
     * it is compelled.
     */
    public static <T, E> Promise<T, E> immediatelyResolve(T value) {
        return new Promise<>((resolver, rejecter) -> {
            resolver.resolve(value);
        });
    }

    /**
     * Returns a new promise which is rejected with {@code error} right after
     * it is compelled.
     *
     * @param error the value to be used as error value.
     * @param <T>   the type of the result of this kind of promise
     * @param <E>   the type of the {@code error}
     * @return a new promise which is rejected with {@code error} right after
     * it is compelled.
     */
    public static <T, E> Promise<T, E> immediatelyReject(E error) {
        return new Promise<>((resolver, rejecter) -> {
            rejecter.reject(error);
        });
    }

    /**
     * Returns a promise of {@link Unit} that is completed when all the promises
     * in {@code promises} are completed.
     *
     * @param promises the promises which completion is awaited for
     * @param <E>      the type of error of the kind of the returned promise
     * @return a promise of {@link Unit} that is completed when all the promises
     * in {@code promises} are completed.
     */
    @SafeVarargs
    public static <E> Promise<Unit, E> allDone(Promise<Unit, E>... promises) {
        return all(Arrays.asList(promises)).map((__) -> Unit.UNIT);
    }

    /**
     * Returns a promise that, when compelled, compels all the given
     * {@code promises} and resolves with a list of the results of the promises.
     * <p></p>
     * Note that if some error occurs in the sub-promises, all the errors are
     * rejected one by one, and the promise never resolves.
     *
     * @param promises the promises compelled in parallel
     * @param <T>      the type of result of the promises
     * @param <E>      the type of error of the promises
     * @return a promise that, when compelled, compels all the given
     * {@code promises} and resolves with a list of the results of the promises.
     */
    public static <T, E>
    Promise<List<T>, E> all(
            Collection<? extends Promise<? extends T, E>> promises
    ) {
        return new Promise<>((res, rej) -> {

            List<T> results = Collections.synchronizedList(new ArrayList<>());
            for (var p : promises) {
                p.then(e -> {
                    results.add(e);
                    if (results.size() == promises.size()) {
                        res.resolve(results);
                    }
                }).onError(rej)
                        .compel();
            }
        });
    }

    /**
     * Returns a promise that, when compelled, compels concurrently all the
     * internal promises, resolves all the result values and rejects all the
     * error values.
     *
     * @param promises the promises of which the returned promises is composed
     *                 of
     * @param <T>      the type of result of the promises
     * @param <E>      the type of error of the promises
     * @return a promise that, when compelled, compels concurrently all the
     * internal promises, resolves all the result values and rejects all the
     * error values.
     */
    public static <T, E>
    Promise<ArrayList<? extends T>, ArrayList<? extends E>> allSettled(
            Collection<Promise<? extends T, ? extends E>> promises
    ) {
        return new Promise<>((res, rej) -> {
            ArrayList<T> results = new ArrayList<>();
            ArrayList<E> errors = new ArrayList<>();
            for (var p : promises) {
                p.then(e -> {
                    results.add(e);
                    if (results.size() + errors.size() == promises.size()) {
                        res.resolve(results);
                        rej.reject(errors);
                    }
                }).onError(e -> {
                    errors.add(e);
                    if (results.size() + errors.size() == promises.size()) {
                        res.resolve(results);
                        rej.reject(errors);
                    }
                }).compel();
            }
        });
    }

    /**
     * Returns a promise that, when compelled, compels concurrently all the
     * provided {@code promises}; then resolves/rejects the value of the
     * <b>first</b> resolved/rejected promise.
     *
     * @param promises the promises to be compelled concurrently
     * @param <T>      the type of the result
     * @param <E>      the type of the error
     * @return a promise that, when compelled, compels concurrently all the
     * provided {@code promises}; then resolves/rejects the value of the
     * <b>first</b> resolved/rejected promise.
     */
    public static <T, E>
    Promise<T, E> race(Collection<Promise<T, E>> promises) {
        return new Promise<>((res, rej) -> {
            AtomicBoolean gotFirst = new AtomicBoolean(false);
            for (var p : promises) {
                p.then(t -> {
                    if (!gotFirst.get()) {
                        res.resolve(t);
                        gotFirst.set(true);
                    }
                }).onError(e -> {
                    if (!gotFirst.get()) {
                        rej.reject(e);
                        gotFirst.set(true);
                    }
                }).compel();
            }
        });
    }


    /**
     * Returns a Promise that executes the provided list of {@link Unit}
     * promises one after the other, until all of them are completed
     * (successfully) or one of them is rejected.
     *
     * @param promises the input list of jobs to perform
     * @return a promise that, when compelled, compels the promises provided in
     * the list, sequentially, in order.
     */
    public static <E> Promise<Unit, E> sequentially(
            List<? extends Promise<Unit, E>> promises
    ) {
        return compose(Unit.UNIT, promises.stream().map((p) ->
                (Function<? super Unit, ? extends Promise<Unit, E>>) __ -> p)
                .collect(Collectors.toList())
        );
    }

    /**
     * Returns a Promise that, when compelled, executes the first function in
     * {@code fs}; the returned promise is compelled, and upon resolution the
     * next function is executed using the resolved value as argument, and so
     * on, until the last promise is resolved. The resolved value of the overall
     * promise is the value resolved by the last promise.
     *
     * @param fs the input list of promise-returning-functions to be composed
     *           using the <i>promise execution model of computation</i>.
     * @return a Promise that, when compelled, executes the first function in
     * {@code fs}; the returned promise is compelled, and upon resolution the
     * next function is executed using the resolved value as argument, and so
     * on, until the last promise is resolved. The resolved value of the overall
     * promise is the value resolved by the last promise.
     */
    public static <T, E> Promise<T, E> compose(
            T initialValue,
            List<Function<? super T, ? extends Promise<T, E>>> fs
    ) {
        if (fs.isEmpty()) {
            return Promises.immediatelyResolve(initialValue);
        } else {
            return compose(initialValue, fs.subList(0, fs.size() - 1))
                    .flatMap(fs.get(fs.size() - 1));

        }
    }

    /**
     * Like {@link #compose(Object, List)}, but the {@code initialValue} is
     * obtained as result of the {@code firstPromise}.
     *
     * @param firstPromise the first promise to be compelled.
     * @param fs           the input list of promise-returning-functions to be
     *                     composed using the <i>promise execution model of
     *                     computation</i>, which will be executed upon
     *                     resolution of the {@code firstPromise}
     * @param <T>          the type of the result of the promise
     * @param <E>          the type of the error of the promise
     */
    public static <T, E> Promise<T, E> compose(
            Promise<T, E> firstPromise,
            List<Function<? super T, ? extends Promise<T, E>>> fs
    ) {
        return firstPromise.flatMap((t) -> compose(t, fs));
    }

    /**
     * Returns a promise that compels the provided {@code bodyAndGuard} promise
     * until it resolves to {@code false}  or it rejects to an error.
     * <p></p>
     * NOTE: do not use this for infinite or very big loops, as it is defined by
     * recursion and might overflow the stack.
     *
     * @param bodyAndGuard the promise used to define the loop.
     * @param <E>          the type of error of the returned promise
     * @return a promise that compels the provided {@code bodyAndGuard} promise
     * until it resolves to {@code false}  or it rejects to an error.
     */
    public static <E> Promise<Unit, E> asyncWhile(
            Promise<Boolean, E> bodyAndGuard
    ) {
        return bodyAndGuard.flatMap((bool) -> {
            if (bool) {
                return asyncWhile(bodyAndGuard);
            } else {
                return immediatelyResolve(Unit.UNIT);
            }
        });
    }

    /**
     * Returns a promise that compels the provided {@code body} promise until
     * the {@code syncGuard} returns true or the promise rejects to an error.
     * <p></p>
     * NOTE: do not use this for infinite or very big loops, as it is defined by
     * recursion and might overflow the stack.
     *
     * @param syncGuard the value expression that will be executed before
     *                  compelling a new promise at each iteration of the loop
     * @param body      the promise to be executed at each iteration
     * @param <E>       the type of error of the returned promise
     * @return a promise that compels the provided {@code body} promise until
     * the {@code syncGuard} returns true or the promise rejects to an error.
     */
    public static <E> Promise<Unit, E> asyncWhile(
            Supplier<Boolean> syncGuard,
            Promise<Unit, E> body
    ) {
        if (syncGuard.get()) {
            return body.flatMap((__) -> asyncWhile(syncGuard, body));
        } else {
            return immediatelyResolve(Unit.UNIT);
        }
    }

    /**
     * Returns a promise that compels the provided {@code body} promise until
     * the {@code asyncGuard} resolves to true true or any of the promises
     * reject to an error.
     * <p></p>
     * NOTE: do not use this for infinite or very big loops, as it is defined by
     * recursion and might overflow the stack.
     *
     * @param asyncGuard promises that will be compelled before compelling a new
     *                   promise at each iteration of the loop
     * @param body       the promise to be executed at each iteration
     * @param <E>        the type of error of the returned promise
     * @return a promise that compels the provided {@code body} promise until
     * the {@code asyncGuard} resolves to true true or any of the promises
     * reject to an error.
     */
    public static <E> Promise<Unit, E> asyncWhile(
            Promise<Boolean, E> asyncGuard,
            Promise<Unit, E> body
    ) {
        return asyncGuard.flatMap((bool) -> {
            if (bool) {
                return body.flatMap((__) -> asyncWhile(asyncGuard, body));
            } else {
                return immediatelyResolve(Unit.UNIT);
            }
        });
    }

    /**
     * Returns a promise that compels the provided {@code body} promise for many
     * iterations; before each iteration a integer value is checked against the
     * {@code guard} predicate. If the predicate returns false, the returned
     * promise is resolved with {@link Unit}. At the end of each iteration the
     * function {@code step} is executed on the integer value.
     * <p></p>
     * NOTE: do not use this for infinite or very big loops, as it is defined by
     * recursion and might overflow the stack.
     */
    public static <E> Promise<Unit, E> asyncFor(
            int start,
            Predicate<Integer> guard,
            IntUnaryOperator step,
            Function<Integer, Promise<Unit, E>> body
    ) {
        AtomicInteger count = new AtomicInteger(start);
        Promise<Unit, E> unitEPromise = immediatelyResolve(Unit.UNIT);
        return asyncWhile(
                () -> guard.test(count.get()),
                unitEPromise
                        .flatMap((__) -> body.apply(count.get()))
                        .then((__) -> count.updateAndGet(step))
        );
    }

    /**
     * Equivalent to
     * {@link #asyncFor(int, Predicate, IntUnaryOperator, Function)
     * asyncFor(0, i-> i< howManyIterations, i -> i + 1, body)}
     * <p></p>
     * NOTE: do not use this for very big loops, as it is defined by recursion
     * and might overflow the stack.
     */
    public static <E> Promise<Unit, E> asyncFor(
            int howManyIterations,
            Function<Integer, Promise<Unit, E>> body
    ) {
        return asyncFor(
                0,
                (i) -> i < howManyIterations,
                (i) -> i + 1,
                body
        );
    }

    /**
     * Like an {@link #asyncWhile(Supplier, Promise)}, but the promise used as
     * body of each iteration is returned by a function ({@code body}) thath
     * takes in input the value resolved by the promise of the previous
     * iteration. The first iteration takes in input {@code initialValue}, and
     * the value resolved by the final promise is the one resolved by the
     * promise of the last iteration. The loop goes on until {@code syncGuard}
     * returns false.
     * <p></p>
     * NOTE: do not use this for infinite or very big loops, as it is defined by
     * recursion and might overflow the stack.
     */
    public static <T, E> Promise<T, E> asyncFeedbackWhile(
            T initialValue,
            Supplier<Boolean> syncGuard,
            Function<T, Promise<T, E>> body
    ) {
        if (syncGuard.get()) {
            return body.apply(initialValue).thenAwait((v) -> asyncFeedbackWhile(
                    v, syncGuard, body
            ));
        } else {
            return immediatelyResolve(initialValue);
        }
    }

    /**
     * Like an {@link #asyncFor(int, Predicate, IntUnaryOperator, Function)},
     * but the promise used as
     * body of each iteration is returned by a function ({@code body}) that
     * takes also in input the value resolved by the promise of the previous
     * iteration. The first iteration takes in input {@code initialValue}, and
     * the value resolved by the final promise is the one resolved by the
     * promise of the last iteration.
     * <p></p>
     * NOTE: do not use this for infinite or very big loops, as it is defined by
     * recursion and might overflow the stack.
     */
    public static <T, E> Promise<T, E> asyncFeedbackFor(
            T initialValue,
            int start,
            Predicate<Integer> guard,
            IntUnaryOperator step,
            BiFunction<T, Integer, Promise<T, E>> body
    ) {
        AtomicInteger count = new AtomicInteger(start);
        return asyncFeedbackWhile(
                initialValue,
                () -> guard.test(count.get()),
                (v) -> body.apply(v, count.get())
                        .then(__ -> count.updateAndGet(step))
        );
    }

    /**
     * Like an {@link #asyncFor(int, Function)} but the promise used as
     * body of each iteration is returned by a function ({@code body}) that
     * takes also in input the value resolved by the promise of the previous
     * iteration. The first iteration takes in input {@code initialValue}, and
     * the value resolved by the final promise is the one resolved by the
     * promise of the last iteration.
     * <p></p>
     * NOTE: do not use this for very big loops, as it is defined by
     * recursion and might overflow the stack.
     */
    public static <T, E> Promise<T, E> asyncFeedbackFor(
            T initialValue,
            int numIterations,
            BiFunction<T, Integer, Promise<T, E>> body
    ) {
        return asyncFeedbackFor(
                initialValue,
                0,
                (i) -> i < numIterations,
                (i) -> i + 1,
                body
        );
    }
}
