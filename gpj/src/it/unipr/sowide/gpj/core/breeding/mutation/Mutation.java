package it.unipr.sowide.gpj.core.breeding.mutation;

import it.unipr.sowide.gpj.core.EvolutionContext;
import it.unipr.sowide.gpj.core.Individual;
import it.unipr.sowide.gpj.core.breeding.BreedingStrategy;
import it.unipr.sowide.util.promise.Promise;
import it.unipr.sowide.util.promise.Promises;

import java.util.ArrayList;
import java.util.List;

/**
 * A breeding strategy that mutates each individual of the population according
 * to a specified {@link MutationOperator}.
 *
 * @param <I> the type of individuals
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class Mutation<I extends Individual> implements BreedingStrategy<I> {
    private final MutationOperator<I> operator;

    /**
     * @param operator the method used to mutate each individual.
     */
    public Mutation(MutationOperator<I> operator) {
        this.operator = operator;
    }

    @Override
    public Promise<List<I>, Throwable> breed(
            EvolutionContext evolutionContext,
            List<I> oldPopulation,
            int targetPopSize
    ) {
        List<I> offspring = new ArrayList<>();
        int mutCount = 0;
        for (int i = 0; i < targetPopSize; i++) {
            I oldInd = oldPopulation.get(i);
            I newInd = operator.mutate(evolutionContext, oldInd);

            offspring.add(newInd);
            if (!oldInd.equals(newInd)) {
                newInd.rebirth(evolutionContext.getCurrentGeneration());
                mutCount++;
            }
        }
        evolutionContext.putStatistic("mutations_count", (double) mutCount);
        return Promises.immediatelyResolve(offspring);
    }
}
