package it.unipr.sowide.actodata.core.dataset;

import it.unipr.sowide.actodata.core.acquirer.Acquirer;
import it.unipr.sowide.actodata.core.actodesext.ActoDataCaseFactory;
import it.unipr.sowide.actodata.core.dataset.content.AcquirerSpawned;
import it.unipr.sowide.actodata.core.dataset.content.SpawnAcquirer;
import it.unipr.sowide.util.Pair;
import it.unipr.sowide.actodes.actor.Behavior;
import it.unipr.sowide.actodes.registry.Reference;

/**
 * A partial specialization of a dataset manager that is able to spawn acquirers
 * that submit all the instances in the dataset.
 *
 * @param <K> the type of the keys used to identify istances of the data
 * @param <V> the type of the instances
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 * @see SpawnAcquirer
 * @see AcquirerSpawned
 */
public abstract class AcquirableDatasetManager<K, V>
        extends DataSetManager<K, V> implements Iterable<Pair<K, V>> {

    /**
     * {@inheritDoc}
     **/
    @Override
    public void datasetManagerCases(ActoDataCaseFactory c) {
        super.datasetManagerCases(c);

        c.onContentOfType(SpawnAcquirer.class, (spawnAcquirer, message) -> {
            Reference r = this.spawnAcquirer();
            send(message, new AcquirerSpawned(r));
        });
    }

    private Reference spawnAcquirer() {
        Behavior b = new Acquirer<Pair<K, V>>() {
            @Override
            public void setupListening(
                    AcquiredDataAcceptor<Pair<K, V>> acceptor
            ) {
                iterator().forEachRemaining(acceptor::submit);
            }
        };

        return actor(b);
    }
}
