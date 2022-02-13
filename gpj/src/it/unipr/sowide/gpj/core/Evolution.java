package it.unipr.sowide.gpj.core;

import it.unipr.sowide.gpj.core.breeding.BreedingStrategy;
import it.unipr.sowide.gpj.core.breeding.selection.Selection;
import it.unipr.sowide.gpj.core.evaluation.PopulationEvaluator;
import it.unipr.sowide.gpj.core.logbook.EvolutionLogbook;
import it.unipr.sowide.util.RandomUtils;
import it.unipr.sowide.util.promise.Promise;
import it.unipr.sowide.util.promise.Promises;
import it.unipr.sowide.util.promise.Unit;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * The process of evolving a set of individuals of type {@link I}.
 *
 * @param <I> the type of individuals
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class Evolution<I extends Individual> {

    public AtomicInteger evaluationsCount = new AtomicInteger(0);

    /**
     * A new evolution
     *
     * @param individualGenerator the generator of individuals
     * @param evaluator           method used to evaluate a population
     * @param breedingStrategy    strategy used to generate a new population
     *                            from an old one
     * @param random              random generator
     * @param popSize             size of the population to be evolved
     */
    public Evolution(
            IndividualGenerator<I> individualGenerator,
            PopulationEvaluator<I> evaluator,
            BreedingStrategy<I> breedingStrategy,
            RandomUtils random,
            int popSize
    ) {
        this.individualGenerator = individualGenerator;
        this.evaluator = evaluator;
        this.breedingStrategy = breedingStrategy;
        this.random = random;
        this.popSize = popSize;
    }

    private final HallOfFame<I> hallOfFame = new HallOfFame<>(10);
    private final EvolutionLogbook logbook = new EvolutionLogbook();

    private final IndividualGenerator<I> individualGenerator;

    private final PopulationEvaluator<I> evaluator;

    private final BreedingStrategy<I> breedingStrategy;

    private final RandomUtils random;
    private final int popSize;

    private final List<I> currentPopulation = new ArrayList<>();

    private final EvolutionContext evolutionContext = new EvolutionContext() {
        @Override
        public RandomUtils getRandom() {
            return random;
        }

        @Override
        public void putStatistic(String key, Double value) {
            logbook.putStatistic(key, value);
        }

        @Override
        public int getCurrentGeneration() {
            return logbook.getGenerationCount();
        }
    };


    private void generatePopulation() {
        while (currentPopulation.size() < popSize) {
            var e = individualGenerator.generateIndividual();
            currentPopulation.add(e);
        }
    }

    /**
     * Creates a new population.
     */
    public void initialize() {
        generatePopulation();

        logbook.logPopulationInitialized(currentPopulation.size());
    }


    /**
     * Returns the current population.
     *
     * @return the current population.
     */
    public List<I> getCurrentPopulation() {
        return currentPopulation;
    }

    /**
     * Adds a listener for text messages that describe relevant events of the
     * evolution process
     *
     * @param consumer the listener
     */
    public void addLogbookOutputListener(Consumer<String> consumer) {
        logbook.addListener(consumer);
    }

    /**
     * Performs an iteration that:<ul>
     * <li>evaluates all the population;</li>
     * <li>if the target fitness is reached, it stops;</li>
     * <li>otherwise, performs the breeding strategy on the population.</li>
     * </ul>
     * <p></p>
     * Note that the call is async and non-blocking. The execution is completed
     * when the returned {@link Promise} is resolved with a boolean value that
     * is true when the target fitess is reached.
     *
     * @param maxFitness the target fitness
     * @return a promise which is resolved a the end of the iteration with a
     * boolean value that is true when the target fitess is reached.
     */
    public Promise<Boolean, Throwable> asyncIteration(Double maxFitness) {
        final DummyTargetIndividual target;
        AtomicBoolean targetReached = new AtomicBoolean(false);
        if (maxFitness != null) {
            target = new DummyTargetIndividual(maxFitness);
        } else {
            target = null;
        }
        return Promises.Do().then((__) -> {
            logbook.logStartGeneration(currentPopulation.size());
        }).thenAwait((__) -> evaluator.updateEvaluationsAsync(currentPopulation)
        ).thenAwait((Integer evaluationsCount) -> {
            this.evaluationsCount.addAndGet(evaluationsCount);
            logbook.logEvaluationsDone(evaluationsCount);
            var currentGenBest = hallOfFame.pushRankings(currentPopulation);
            if (currentGenBest != null) {
                logbook.logGenerationBest(currentGenBest);
            }
            logbook.logAllTimeBest(hallOfFame.getBestIndividuals().first());
            if (target != null && currentGenBest != null
                    && currentGenBest.getFitness().isPresent()
                    && currentGenBest.compareTo(target) <= 0) {
                logbook.logTargetFitnessReached(currentGenBest);
                targetReached.set(true);
                return Promises.immediatelyResolve(null);
            } else {
                return breedingStrategy.breed(
                        evolutionContext,
                        currentPopulation,
                        currentPopulation.size()
                );
            }
        }).then((offspring) -> {
            if (offspring != null) {
                currentPopulation.clear();
                currentPopulation.addAll(offspring);
            }
        }).map((__) -> targetReached.get());
    }

    /**
     * Performs an iteration that:<ul>
     * <li>evaluates all the population;</li>
     * <li>performs the breeding strategy on the population.</li>
     * </ul>
     * <p></p>
     * Note that the call is async and non-blocking. The execution is completed
     * when the returned {@link Promise} is resolved with a {@link Unit} value.
     *
     * @return a promise which is resolved a the end of the iteration.
     */
    public Promise<Unit, Throwable> asyncIteration() {
        return asyncIteration(null).map((__) -> Unit.UNIT);
    }

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition syncIterationCondition = lock.newCondition();

    /**
     * Performs an iteration that:<ul>
     * <li>evaluates all the population;</li>
     * <li>if the target fitness is reached, it stops;</li>
     * <li>otherwise, performs the breeding strategy on the population.</li>
     * </ul>
     * <p></p>
     *
     * @param targetFitness the target fitness
     * @return a boolean value that is true when the target fitess is reached.
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean syncIteration(Double targetFitness)
            throws InterruptedException {
        AtomicBoolean result = new AtomicBoolean();
        AtomicBoolean done = new AtomicBoolean(false);
        asyncIteration(targetFitness)
                .then(result::set)
                .then((__) -> {
                    done.set(true);
                    try {
                        lock.lock();
                        syncIterationCondition.signal();
                    } finally {
                        lock.unlock();
                    }
                })
                .compel();
        while (!done.get()) {
            try {
                lock.lock();
                syncIterationCondition.await();
            } finally {
                lock.unlock();
            }
        }
        return result.get();
    }

    /**
     * Performs an iteration that:<ul>
     * <li>evaluates all the population;</li>
     * <li>performs the breeding strategy on the population.</li>
     * </ul>
     */
    public void syncIteration() throws InterruptedException {
        syncIteration(null);
    }

    /**
     * Performs the specified number of iterations of the evolution algorithm.
     *
     * @param generations the number of iterations.
     * @throws InterruptedException if the thread is interrupted while an
     *                              iteration is being executed.
     */
    public void evolveSync(int generations) throws InterruptedException {
        for (int i = 0; i < generations; i++) {
            syncIteration();
        }
    }

    /**
     * Performs the specified number of iterations of the evolution algorithm.
     * The evolution stops if the {@code maxFitness} is reached.
     *
     * @param generations the number of iterations.
     * @throws InterruptedException if the thread is interrupted while an
     *                              iteration is being executed.
     */
    public void evolveSync(int generations, Double maxFitness)
            throws InterruptedException {
        for (int i = 0; i < generations; i++) {
            syncIteration(maxFitness);
        }
    }

    /**
     * Selects the specified number of individuals via the {@link Selection}
     * operator, and returns a promise which resolves with the list of
     * individuals.
     *
     * @param migrantsSelection the selection operator that selects the
     *                          individuals
     * @param howManyEmigrants  how many migrants to select
     * @return a promise that resolves with the list of migrants.
     */
    public Promise<List<I>, Throwable> emigrate(
            Selection<I> migrantsSelection,
            int howManyEmigrants
    ) {
        return migrantsSelection.select(
                evolutionContext,
                currentPopulation,
                howManyEmigrants
        ).then((emigrants) -> {
            for (I emigrant : emigrants) {
                currentPopulation.remove(emigrant);
                hallOfFame.getBestIndividuals().remove(emigrant);
            }
        });
    }

    /**
     * Adds the specified individuals to the population.
     *
     * @param immigrants the immigrants
     */
    public void immigrate(List<I> immigrants) {
        currentPopulation.addAll(immigrants);
        hallOfFame.pushRankings(immigrants);
    }

    /**
     * @return the hall of fame
     */
    public HallOfFame<I> getHallOfFame() {
        return hallOfFame;
    }

    /**
     * @return the logbook
     */
    public EvolutionLogbook getLogbook() {
        return logbook;
    }


    private final static class DummyTargetIndividual implements Individual {

        private final double targetFitness;


        public DummyTargetIndividual(double targetFitness) {
            this.targetFitness = targetFitness;
        }

        @Override
        public Promise<Individual, Throwable> copyIndividual() {
            return Promises.immediatelyResolve(
                    new DummyTargetIndividual(targetFitness)
            );
        }

        @Override
        public int getBirthGeneration() {
            return 0;
        }

        @Override
        public void setBirthGeneration(int gen) {
        }

        @Override
        public Optional<Double> getFitness() {
            return Optional.of(targetFitness);
        }

        @Override
        public void setFitness(double value) {

        }

        @Override
        public void resetFitness() {

        }

        @Override
        public void kill() {

        }

        @Override
        public boolean isInHallOfFame() {
            return false;
        }

        @Override
        public void setInHallOfFame(boolean inHallOfFame) {

        }
    }
}
