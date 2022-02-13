package it.unipr.sowide.gpj.core.breeding;

import it.unipr.sowide.gpj.core.EvolutionContext;
import it.unipr.sowide.gpj.core.Individual;
import it.unipr.sowide.gpj.core.breeding.selection.Selection;
import it.unipr.sowide.util.Pair;
import it.unipr.sowide.util.Tuple3;
import it.unipr.sowide.util.promise.Promise;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Wraps another breeding strategy. Before the breeding, selects a specific
 * number of individuals with the provided selection operator. After the
 * breeding, these individuals replace those individuated by another selection
 * operator. The replaced individuals are "killed"
 * (via {@link Individual#kill()}).
 *
 * @param <I> the type of individuals
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class PreserveIndividuals<I extends Individual>
        implements BreedingStrategy<I> {

    private final int howManySurvivors;
    private final Selection<I> survivalSelection;
    private final Selection<I> dyingSelection;
    private final BreedingStrategy<I> restOfBreeding;

    /**
     * @param howManySurvivors  how many individuals will be preserved
     * @param survivalSelection the selection method for the survivors
     * @param dyingSelection    the selection method for the dying individuals
     * @param restOfBreeding    the wrapped breeding strategy
     */
    public PreserveIndividuals(
            int howManySurvivors,
            Selection<I> survivalSelection,
            Selection<I> dyingSelection,
            BreedingStrategy<I> restOfBreeding
    ) {
        this.howManySurvivors = howManySurvivors;
        this.survivalSelection = survivalSelection;
        this.dyingSelection = dyingSelection;
        this.restOfBreeding = restOfBreeding;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public Promise<List<I>, Throwable> breed(
            EvolutionContext evolutionContext,
            List<I> oldPopulation,
            int targetPopSize
    ) {
        return survivalSelection.select(
                evolutionContext,
                oldPopulation,
                howManySurvivors
        ).thenAwait((selected) -> {
            List<I> survivors = new ArrayList<>(selected);
            return restOfBreeding.breed(
                    evolutionContext,
                    oldPopulation,
                    targetPopSize
            ).map((offspring) -> Pair.pair(survivors, offspring));
        }).thenAwait((pair) -> dyingSelection.select(
                evolutionContext,
                pair.get2(),
                howManySurvivors
                ).map((dying) -> new Tuple3<>(pair.get1(), pair.get2(), dying))
        ).map((triple) -> {
            Set<I> dying = new HashSet<>(triple.get_3());
            var survivors = triple.get_1();
            var offspring = triple.get_2();
            int survCount = 0;
            for (int j = 0;
                 j < offspring.size()
                         && !survivors.isEmpty()
                         && survCount < howManySurvivors;
                 j++) {
                if (dying.contains(offspring.get(j))) {
                    var dead = offspring.set(j, survivors.remove(0));
                    dead.kill();
                    survCount++;
                }
            }

            evolutionContext.putStatistic("survivors", (double) survCount);
            return offspring;
        });
    }
}
