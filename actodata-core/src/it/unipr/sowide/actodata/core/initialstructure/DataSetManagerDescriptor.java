package it.unipr.sowide.actodata.core.initialstructure;

import it.unipr.sowide.actodata.core.dataset.DataSetManager;
import it.unipr.sowide.actodata.core.master.Master;
import it.unipr.sowide.actodata.core.master.nodeinterfaces.DataSetManagerInterface;
import it.unipr.sowide.actodata.core.master.nodeinterfaces.NodeInterface;
import it.unipr.sowide.actodes.registry.Reference;

import java.util.function.Supplier;

/**
 * A descriptor for the declaration of a dataset manager, used by a
 * {@link Master} and with {@link ActoDataStructure} to build the dataset
 * manager.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class DataSetManagerDescriptor<K, V> implements NodeDescriptor {
    private final String name;
    private final Supplier<DataSetManager<K, V>> datasetManagerSupplier;

    public DataSetManagerDescriptor(
            String name,
            Supplier<DataSetManager<K, V>> datasetManagerSupplier
    ) {
        this.name = name;
        this.datasetManagerSupplier = datasetManagerSupplier;
    }

    public DataSetManagerDescriptor(
            Supplier<DataSetManager<K, V>> datasetManagerSupplier
    ) {
        this.name = null;
        this.datasetManagerSupplier = datasetManagerSupplier;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public NodeInterface createActor(Master creator) {
        DataSetManager<K, V> behavior = datasetManagerSupplier.get();
        Reference ref = creator.actor(behavior);
        if (isNamed()) {
            return new DataSetManagerInterface(
                    creator,
                    name,
                    ref,
                    behavior.getClass()
            );
        } else {
            return new DataSetManagerInterface(
                    creator,
                    ref,
                    behavior.getClass()
            );
        }
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public boolean isNamed() {
        return name != null;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public String getName() {
        return name;
    }


}
