package it.unipr.sowide.gpj.core.breeding.mating;

import it.unipr.sowide.util.Generator;
import it.unipr.sowide.util.Pair;
import it.unipr.sowide.gpj.core.EvolutionContext;
import it.unipr.sowide.gpj.core.Individual;
import it.unipr.sowide.gpj.core.breeding.BreedingStrategy;
import it.unipr.sowide.util.RandomUtils;
import it.unipr.sowide.util.promise.Promise;
import it.unipr.sowide.util.promise.Promises;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A breeding strategy that extracts couples from the population ad mates them
 * in order to produce new individuals.
 *
 * @param <I> the type of individuals
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class Mating<I extends Individual> implements BreedingStrategy<I> {
    private final MateOperator<I> operator;

    /**
     * A Mating that uses the specified {@link MateOperator} to mate each
     * couple.
     *
     * @param operator the mate operator
     */
    public Mating(MateOperator<I> operator) {
        this.operator = operator;
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
        Generator<Pair<I, I>> pairGenerator =
                new InfiniteMatingStrategy<I>(evolutionContext.getRandom())
                        .generatePairs(oldPopulation);

        int cxCount = 0;
        List<I> offspring = new ArrayList<>();
        for (Pair<I, I> parents : pairGenerator) {

            Pair<I, I> children =
                    operator.mate(evolutionContext, parents);

            if (!children.equals(parents)) {
                Pair.doOnBoth(children, c -> {
                    c.rebirth(evolutionContext.getCurrentGeneration());
                });
                cxCount++;
            }

            if (offspring.size() < targetPopSize) {
                I child = children.get1();
                offspring.add(child);
            } else {
                break;
            }

            if (offspring.size() < targetPopSize) {
                I child = children.get2();
                offspring.add(child);
            } else {
                break;
            }
        }

        evolutionContext.putStatistic("mate_count", (double) cxCount);
        return Promises.immediatelyResolve(offspring);
    }

    private static class InfiniteMatingStrategy<I extends Individual> {
        private final RandomUtils random;

        public InfiniteMatingStrategy(RandomUtils random) {
            this.random = random;
        }

        public Generator<Pair<I, I>>
        generatePairs(List<I> pop) {
            var counter = new AtomicInteger(0);

            return new Generator<>(() -> {
                int i = counter.get();
                counter.set(counter.get() + 2);
                if (i < pop.size() - 1) {
                    I parent1 = pop.get(i);
                    I parent2 = pop.get(i + 1);
                    return Optional.of(Pair.of(parent1, parent2));
                } else {
                    I pick1 = random.pick(pop);
                    I pick2 = random.pick(pop);
                    return Optional.of(Pair.of(pick1, pick2));
                }
            });

        }


    }
}
