package it.unipr.sowide.gpj.core;

import it.unipr.sowide.util.Require;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Contains the best individuals encountered in an evolution.
 *
 * @param <I> the type of individuals.
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class HallOfFame<I extends Individual> {

    private final int size;
    private final SortedSet<I> hall = new TreeSet<>();

    /**
     * An hall of fame that keeps the specified number of best individuals.
     *
     * @param size the size of the hall of fame
     */
    public HallOfFame(int size) {
        Require.strictlyPositive(size);
        this.size = size;
    }

    /**
     * Method used update the hall of fame, and, in the process, retrieve the
     * best individual of the provided population.
     *
     * @param pop the population used to update the hall of fame
     * @return the best individual found in {@code pop}
     */
    public I pushRankings(List<I> pop) {
        I submittedBest = null;
        for (I ind : pop) {
            if (submittedBest == null) {
                submittedBest = ind;
            } else {
                if (submittedBest.compareTo(ind) > 0) {
                    submittedBest = ind;
                }
            }
            hall.add(ind);
            if (hall.size() > size) {
                I last = hall.last();
                last.setInHallOfFame(false);
                hall.remove(last);
            }
        }
        hall.forEach(i -> i.setInHallOfFame(true));
        return submittedBest;
    }

    /**
     * Returns a sorted set with the best individuals. The first individual in
     * the set is the best fit.
     *
     * @return the best individuals
     */
    public SortedSet<I> getBestIndividuals() {
        return hall;
    }

    /**
     * @return the best individual in the hall of fame
     */
    public I bestIndividual() {
        return getBestIndividuals().first();
    }
}
