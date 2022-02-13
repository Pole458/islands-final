package it.unipr.sowide.gpj.core.breeding;

import it.unipr.sowide.gpj.core.EvolutionContext;
import it.unipr.sowide.gpj.core.Individual;
import it.unipr.sowide.util.promise.Promise;
import it.unipr.sowide.util.promise.Promises;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Simply transfomrs the population by copying random individuals from the old
 * population until the target size is reached
 *
 * @param <I> the type of individuals
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class FillingReproduction<I extends Individual>
        implements BreedingStrategy<I> {
    @Override
    public Promise<List<I>, Throwable> breed(
            EvolutionContext evolutionContext,
            List<I> oldPopulation,
            int targetPopSize
    ) {
        int repCount = 0;

        List<Promise<Individual, Throwable>> subPromises = new ArrayList<>();

        for (int i = 0; i < targetPopSize; i++) {
            I pick = evolutionContext.getRandom().pick(oldPopulation);
            if (pick != null) {
                subPromises.add(pick.copyIndividual());
            }
            repCount++;
        }
        evolutionContext.putStatistic("reproduction_count", (double) repCount);
        return Promises.all(subPromises).map((individuals) ->
                individuals.stream()
                        .map(i -> {
                            @SuppressWarnings("unchecked") I casted = (I) i;
                            return casted;
                        })
                        .collect(Collectors.toList())
        );
    }
}
