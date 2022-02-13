package it.unipr.sowide.gpj.core;

import it.unipr.sowide.gpj.core.breeding.BreedingStrategy;
import it.unipr.sowide.util.RandomUtils;

/**
 * Set of operations used internally by {@link BreedingStrategy}s which allow
 * to access to part of the state of the evolution process.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public interface EvolutionContext {
    /**
     * @return the random generator used by this evolution.
     */
    RandomUtils getRandom();

    /**
     * Sets a <i>purely informational</i> statistic to a certain value.
     *
     * @param key   the name of the statistic
     * @param value the value
     */
    void putStatistic(String key, Double value);

    /**
     * @return the current generation number of the evolution
     */
    int getCurrentGeneration();
}
