package it.unipr.sowide.gpj.core;

import it.unipr.sowide.util.promise.Promise;

import java.util.Comparator;
import java.util.Optional;

/**
 * An individual is a part of a population that can be evolved.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public interface Individual extends Comparable<Individual> {
    /**
     * @return promise that resolves with a copy of this individual
     */
    Promise<Individual, Throwable> copyIndividual();

    /**
     * Returns the generation in which this individual is considered to be
     * "born".
     * {@link #copyIndividual()} should not change this.
     *
     * @return the generation
     */
    int getBirthGeneration();

    /**
     * Sets the generation in which this individual is considered to be "born".
     *
     * @param gen the generation
     */
    void setBirthGeneration(int gen);

    /**
     * @return an optional containing the fitness value of the individual, if
     * the individual has already been evaluated; {@link Optional#empty()}
     * otherwise.
     */
    Optional<Double> getFitness();

    /**
     * Sets the fitness of this individual.
     * {@link #copyIndividual()} should not change this.
     *
     * @param value the fitness.
     */
    void setFitness(double value);

    /**
     * Removes the stored fitness for this individual. Calls to
     * {@link #getFitness()} should return {@link Optional#empty()} after this.
     */
    void resetFitness();

    /**
     * Notifies the individual that he is no longer part of a population.
     *
     * @implSpec If the individuals holds resources to work, release them in
     * the implementation of this method.
     */
    void kill();

    /**
     * @return true if the individual is in the hall of fame of its population.
     */
    boolean isInHallOfFame();

    /**
     * Sets wheter if the individual is in the hall of fame of its population.
     */
    void setInHallOfFame(boolean inHallOfFame);

    /**
     * Removes the computed fitness and sets the specified generation as birth
     * generation.
     *
     * @param generation the birth generation.
     */
    default void rebirth(int generation) {
        resetFitness();
        setBirthGeneration(generation);
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    default int compareTo(Individual o) {
        return comparator().compare(this, o);
    }

    /**
     * @return compares individual, such that if a list is sorted (with
     * {@link java.util.List#sort(Comparator)} with this comparator, the
     * <b>first</b> element is the <b>best</b> fit individual.
     */
    default Comparator<Individual> comparator() {
        return MAXIMIZE;
    }

    /**
     * Comparator used to indicate that the evolution solves a maximization
     * problem on the fitness function.
     */
    Comparator<Individual> MAXIMIZE = (i1, i2) -> {
        Optional<Double> f1Opt = i1.getFitness();
        Optional<Double> f2Opt = i2.getFitness();
        if (f1Opt.isPresent() && f2Opt.isPresent()) {
            return Double.compare(f2Opt.get(), f1Opt.get());
        } else if (f1Opt.isPresent()) {
            return 1;
        } else if (f2Opt.isPresent()) {
            return -1;
        } else {
            return 0;
        }
    };

    /**
     * Comparator used to indicate that the evolution solves a minimization
     * problem on the fitness function.
     */
    Comparator<Individual> MINIMIZE = (i1, i2) -> -MAXIMIZE.compare(i1, i2);

}
