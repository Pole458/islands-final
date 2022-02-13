package it.unipr.sowide.gpj.core;

/**
 * Defines the method used to generate an individual.
 *
 * @param <I> the type of individual
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public interface IndividualGenerator<I extends Individual> {
    /**
     * Generates a new individual.
     *
     * @return the individual
     */
    I generateIndividual();
}
