package it.unipr.sowide.actodata.core.dataset;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A {@link DataSetManager} that manages instances of data by putting them
 * inside an {@link ArrayList}.
 *
 * @param <T> the type of the instances
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class ArrayListDataset<T> extends DataSetManager<Integer, T> {
    private final List<T> list = new ArrayList<>();

    /**
     * {@inheritDoc}
     **/
    @Override
    public Optional<T> get(Integer key) {
        if (key >= 0 && key < list.size()) {
            return Optional.ofNullable(list.get(key));
        } else {
            return Optional.empty();
        }
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public void setup() {
        list.clear();
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public Integer add(T instance) {
        int ind = list.size();
        list.add(instance);
        return ind;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public void put(Integer key, T instance) {
        if (key >= 0 && key < list.size()) {
            list.set(key, instance);
        }
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public long size() {
        return list.size();
    }
}
