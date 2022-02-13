package it.unipr.sowide.actodata.core.preprocessor.reduction;

import it.unipr.sowide.actodata.core.preprocessor.Preprocessor;
import it.unipr.sowide.util.Require;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * A {@link Preprocessor} that reduces the input values by inserting them into
 * limited Java mutable {@link Collection}s.
 *
 * @param <T> input data type
 * @param <C> the output {@link Collection}
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class LimitCollector<T, C extends Collection<T>>
        extends JavaMutableCollector<T, C> {

    private final int limit;
    private final Supplier<C> collectionSupplier;

    /**
     * A collector that creates collections of size specified by {@code limit}
     * using the provided {@code collectionSupplier}.
     *
     * @param limit              the size of the resulting collections
     * @param collectionSupplier the supplier for empty collections
     */
    public LimitCollector(int limit, Supplier<C> collectionSupplier) {
        this.collectionSupplier = collectionSupplier;
        Require.strictlyPositive(limit);
        this.limit = limit;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public boolean shouldCreateNewCollection(C currentAccumulator) {
        return currentAccumulator.size() >= limit;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public C createAccumulator() {
        return collectionSupplier.get();
    }
}
