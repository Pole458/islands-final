package it.unipr.sowide.actodata.core.actodesext.promise;

import it.unipr.sowide.actodes.interaction.Done;
import it.unipr.sowide.actodes.interaction.Error;
import it.unipr.sowide.util.annotations.Namespace;
import it.unipr.sowide.util.promise.Promise;
import it.unipr.sowide.util.promise.Promises;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static it.unipr.sowide.actodata.core.actodesext.promise.ActoPromise.actoPromise;
import static it.unipr.sowide.util.promise.Promises.*;

/**
 * Set of utility static methods to build and operate {@link ActoPromise}s.
 * Most of them are implemented by wrapping or converting the results of the
 * relative methods in {@link Promises} into ActoPromises.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
@Namespace
public class ActoPromises {
    private ActoPromises() {
    } // do not instantiate

    /**
     * @see Promises#all(Collection)
     */
    public static <T>
    ActoPromise<List<T>> all(
            Collection<? extends Promise<? extends T, Error>> promises
    ) {
        return actoPromise(Promises.all(promises));
    }

    /**
     * @see Promises#immediatelyResolve(Object)
     */
    public static <T> ActoPromise<T> immediatelyResolve(T data) {
        return actoPromise(Promises.immediatelyResolve(data));
    }

    /**
     * @see Promises#immediatelyReject(Object)
     */
    public static <T> ActoPromise<T> immediatelyReject(Error error){
        return actoPromise(Promises.immediatelyReject(error));
    }

    /**
     * @see Promises#sequentially(List)
     */
    public static ActoPromise<Done> sequentially(
            List<? extends Promise<Done, Error>> promises
    ) {
        return actoPromise(compose(Done.DONE, promises.stream().map((p) ->
                (Function<? super Done, ? extends Promise<Done, Error>>)
                        __ -> p)
                .collect(Collectors.toList())
        ));
    }

    /**
     * @see Promises#Do()
     */
    public static ActoPromise<Done> Do() {
        return immediatelyResolve(Done.DONE);
    }
}
