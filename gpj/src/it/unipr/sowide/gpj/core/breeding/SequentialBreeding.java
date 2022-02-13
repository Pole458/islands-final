package it.unipr.sowide.gpj.core.breeding;

import it.unipr.sowide.gpj.core.EvolutionContext;
import it.unipr.sowide.gpj.core.Individual;
import it.unipr.sowide.util.RandomUtils;
import it.unipr.sowide.util.promise.Promise;
import it.unipr.sowide.util.promise.Promises;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A breeding strategy that wraps several other breeding strategies.
 * All the sub-strategies ("phases") are executed one after the other, feeding
 * the result of the previous one as input of the next one.
 *
 * @param <I> the type of individuals
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class SequentialBreeding<I extends Individual>
        implements BreedingStrategy<I> {

    private final List<BreedingStrategy<I>> subPhases = new ArrayList<>();


    public SequentialBreeding<I> addBreedingPhase(BreedingStrategy<I> phase) {
        this.subPhases.add(phase);
        return this;
    }


    @Override
    public Promise<List<I>, Throwable> breed(
            EvolutionContext evolutionContext,
            List<I> oldPopulation,
            int targetPopSize
    ) {
        int subpSize = subPhases.size();
        ConcurrentHashMap<String, Double> stats = new ConcurrentHashMap<>();
        return Promises.Do(

        ).map((__) -> oldPopulation
        ).thenAwait((oldPop) -> Promises.asyncFeedbackFor(
                oldPop,
                subpSize,
                (offspring, i) -> {
                    BreedingStrategy<I> subPhase = subPhases.get(i);
                    var subInterface = getSubInterface(
                            evolutionContext,
                            stats,
                            i
                    );
                    return subPhase.breed(
                            subInterface,
                            offspring,
                            targetPopSize
                    );
                }
        )).then((__)-> stats.forEach(evolutionContext::putStatistic));
    }


    private EvolutionContext getSubInterface(
            EvolutionContext evolutionContext,
            Map<String, Double> statistics,
            int genI
    ) {
        return new EvolutionContext() {
            @Override
            public RandomUtils getRandom() {
                return evolutionContext.getRandom();
            }

            @Override
            public void putStatistic(String key, Double value) {
                String k = "phase" + genI + "_" + key;
                if (statistics.containsKey(k)) {
                    statistics.put(k, statistics.get(k) + value);
                } else {
                    statistics.put(k, value);
                }
            }

            @Override
            public int getCurrentGeneration() {
                return evolutionContext.getCurrentGeneration();
            }
        };
    }
}
