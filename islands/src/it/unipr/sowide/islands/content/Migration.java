package it.unipr.sowide.islands.content;

import it.unipr.sowide.actodes.interaction.Request;
import it.unipr.sowide.gpj.core.Individual;
import it.unipr.sowide.islands.IslandEnvironment;
import it.unipr.sowide.islands.IslandIndividual;

import java.util.*;

/**
 * A message used by islands to send individuals and/or instances of data to
 * other islands.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */

public class Migration implements Request {
    private final List<IslandIndividual> individuals = new ArrayList<>();
    private final List<IslandEnvironment.Instance> instances = new ArrayList<>();


    public Migration(
            Collection<IslandIndividual> individuals,
            Collection<IslandEnvironment.Instance> instances
    ) {
        this.individuals.addAll(individuals);
        this.instances.addAll(instances);
    }

    /**
     * @return the individuals to migrate
     */
    public List<IslandIndividual> getIndividuals() {
        return individuals;
    }

    /**
     * @return the data instances to send
     */
    public Collection<IslandEnvironment.Instance> getInstances() {
        return instances;
    }
}
