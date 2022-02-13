package it.unipr.sowide.gpj.core.breeding;

import it.unipr.sowide.gpj.core.EvolutionContext;
import it.unipr.sowide.gpj.core.Individual;
import it.unipr.sowide.util.RandomUtils;
import it.unipr.sowide.util.promise.Promise;
import it.unipr.sowide.util.promise.Promises;

import java.util.*;
import java.util.stream.Collectors;

/**
 * At each breeding, splits the population into three sub-populations (the
 * ordering of the input population is kept) and breeds each one of them
 * indipendently with a different breeding strategy.
 *
 * @param <I> the type of individuals
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class PartitionedBreeding<I extends Individual>
        implements BreedingStrategy<I> {

    private final Map<String, BreedingStrategy<I>> strategies = new HashMap<>();
    private final Map<String, Double> rates = new HashMap<>();

    /**
     * Defines a "partition" of the population wich breeds indipendently from
     * the others.
     *
     * @param name the name of the partition (for logging purposes)
     * @param op   the breeding strategy used to breed the partition
     * @param rate a weight used to compute the actual size of the partition
     * @return this object for chained calls
     */
    public PartitionedBreeding<I> addPartition(
            String name, BreedingStrategy<I> op, double rate
    ) {
        rates.put(name, rate);
        strategies.put(name, op);
        return this;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public Promise<List<I>, Throwable> breed(
            EvolutionContext evolutionContext,
            List<I> oldPopulation,
            int targetSize
    ) {
        Map<String, Double> statistics = new HashMap<>();

        double sumRates = rates.values().stream().mapToDouble(i -> i).sum();
        Map<String, Integer> sizes = new HashMap<>();

        int portionsSum = 0;
        for (Map.Entry<String, Double> entry : rates.entrySet()) {
            String name = entry.getKey();
            Double rate = entry.getValue();
            int portionSize = (int) (((double) targetSize) * rate / sumRates);
            sizes.put(name, portionSize);
            portionsSum += portionSize;
        }

        if (portionsSum < targetSize) {
            int missing = targetSize - portionsSum;
            String key = sizes.keySet().stream().findAny().orElseThrow();
            sizes.put(key, sizes.get(key) + missing);
        }
        if (portionsSum > targetSize) {
            int excess = portionsSum - targetSize;
            String key = sizes.keySet().stream().findAny().orElseThrow();
            sizes.put(key, sizes.get(key) - excess);
        }


        List<Promise<List<I>, Throwable>> subPromises = new ArrayList<>();
        for (Map.Entry<String, Integer> sizeEntry : sizes.entrySet()) {
            String name = sizeEntry.getKey();
            Integer size = sizeEntry.getValue();
            var strategy = strategies.get(name);
            var subInterface = getSubInterface(
                    evolutionContext,
                    statistics,
                    name
            );

            subPromises.add(strategy.breed(
                    subInterface,
                    oldPopulation,
                    size
            ));
        }

        statistics.forEach(evolutionContext::putStatistic);

        return Promises.all(subPromises)
                .map((manyLists) -> manyLists.stream()
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList()));
    }

    private EvolutionContext getSubInterface(
            EvolutionContext evolutionContext,
            Map<String, Double> statistics,
            String name
    ) {
        return new EvolutionContext() {
            @Override
            public RandomUtils getRandom() {
                return evolutionContext.getRandom();
            }

            @Override
            public void putStatistic(String key, Double value) {
                String k = name + "_" + key;
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
