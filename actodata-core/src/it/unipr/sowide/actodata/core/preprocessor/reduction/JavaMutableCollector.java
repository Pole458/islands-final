package it.unipr.sowide.actodata.core.preprocessor.reduction;

import it.unipr.sowide.actodata.core.preprocessor.Preprocessor;

import java.util.Collection;

/**
 * A {@link Preprocessor} that reduces the input values by inserting them into
 * Java mutable {@link Collection}s.
 *
 * @param <T> input data type
 * @param <C> the output {@link Collection}
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public abstract class JavaMutableCollector<T, C extends Collection<T>>
        extends Reducer<T, C, C> {


    /**
     * {@inheritDoc}
     **/
    @Override
    public C fold(C collection, T element) {
        collection.add(element);
        return collection;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public C finish(C accumulatedData) {
        return accumulatedData;
    }
}
