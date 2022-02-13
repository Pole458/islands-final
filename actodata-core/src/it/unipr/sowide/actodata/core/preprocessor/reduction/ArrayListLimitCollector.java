package it.unipr.sowide.actodata.core.preprocessor.reduction;

import it.unipr.sowide.actodata.core.preprocessor.Preprocessor;

import java.util.ArrayList;

/**
 * A {@link Preprocessor} that reduces the input values by inserting them into
 * array lists.
 *
 * @param <T> input data type
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class ArrayListLimitCollector<T>
        extends LimitCollector<T, ArrayList<T>> {
    /**
     * A collector that creates {@link ArrayList}s of size specified by
     * {@code limit}.
     *
     * @param limit              the size of the resulting collections
     */
    public ArrayListLimitCollector(int limit) {
        super(limit, ArrayList::new);
    }
}
