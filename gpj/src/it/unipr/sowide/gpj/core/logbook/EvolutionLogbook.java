package it.unipr.sowide.gpj.core.logbook;

import it.unipr.sowide.gpj.core.Individual;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * The evolution logbook. Contains history events and statistics of an
 * evolution.
 */
public class EvolutionLogbook {
    private final List<EvolutionLogbookEntry> entries = new ArrayList<>();
    private int generationCount = 0;
    private final List<Consumer<String>> outputListeners = new ArrayList<>();
    private Map<String, Double> statistics = null;

    /**
     * Adds the entry in the logbook
     *
     * @param entry the entry
     */
    private void addEntry(EvolutionLogbookEntry entry) {
        entries.add(entry);
        for (Consumer<String> outputListener : outputListeners) {
            outputListener.accept(entry.toOutput());
        }
    }

    /**
     * Adds an entry that logs that the population has been initialized.
     *
     * @param popSize the size of the population.
     */
    public void logPopulationInitialized(int popSize) {
        generationCount = 0;
        addEntry(new PopInitializedEntry(generationCount, popSize));
    }

    /**
     * Allows to logger systems to receive text messages corresponding to entries
     * added to this logbook, as they are provided.
     *
     * @param listener the listener
     */
    public void addListener(Consumer<String> listener) {
        outputListeners.add(listener);
    }

    /**
     * Logs a simple generic information
     *
     * @param info the information
     */
    public void logInfo(String info) {
        addEntry(new SimpleInfoEntry(generationCount, info));
    }

    /**
     * Logs that a generation has started.
     *
     * @param currentPopSize the size of the current population
     */
    public void logStartGeneration(int currentPopSize) {
        if (statistics != null) {
            addEntry(new GenerationStatisticsEntry(generationCount, statistics));
        }

        statistics = new HashMap<>();
        generationCount++;
        addEntry(new NewGenerationEntry(generationCount, currentPopSize));
    }

    /**
     * @return the current generation counter
     */
    public int getGenerationCount() {
        return generationCount;
    }

    /**
     * @return the entries in this logbook as a {@link List} of
     * {@link EvolutionLogbookEntry}s.
     */
    public List<EvolutionLogbookEntry> getEntries() {
        return entries;
    }

    /**
     * Logs the best individual of the current generation.
     *
     * @param currentBest the current best
     */
    public void logGenerationBest(Individual currentBest) {
        addEntry(new GenerationBestIndividualEntry(currentBest, generationCount));
    }

    /**
     * Logs the best all-time individual at the current generation.
     *
     * @param first the current best
     */
    public void logAllTimeBest(Individual first) {
        addEntry(new AllTimeBestIndividualEntry(first, generationCount));
    }

    /**
     * Logs that the target fitness has been reached.
     *
     * @param currentBest the best individual found.
     */
    public void logTargetFitnessReached(Individual currentBest) {
        addEntry(new TargetFitnessReachedEntry(currentBest, generationCount));
    }

    /**
     * Sets the statistic with the specified value for this generation.
     *
     * @param key   the name of the statistic
     * @param value the value assigned to the statistic
     */
    public void putStatistic(String key, Double value) {
        if (statistics == null) {
            statistics = new HashMap<>();
        }

        statistics.put(key, value);
    }

    /**
     * Logs that the update of fitnesses is completed.
     * @param evaluations the actual number of evaluations performed.
     */
    public void logEvaluationsDone(int evaluations) {
        addEntry(new EvaluationsDoneEntry(evaluations, generationCount));
    }
}
