import it.unipr.sowide.gpj.core.EvolutionContext;
import it.unipr.sowide.islands.IslandEngine;
import it.unipr.sowide.islands.IslandEnvironment;
import it.unipr.sowide.islands.IslandIndividual;
import it.unipr.sowide.islands.settings.IslandsSimulationSettings;
import it.unipr.sowide.util.Pair;
import it.unipr.sowide.util.RandomUtils;

public class AverageIslandEngine extends IslandEngine {

    /**
     * An engine that evolves the classifiers using the specified
     * hyperparameters.
     *
     * @param trainingEnv   the set of data used to evolve the classifiers
     * @param validationEnv the set of data used to validate the
     *                      classifiers
     * @param settings      settings
     * @param random        a random generation utility instance
     */
    public AverageIslandEngine(IslandEnvironment trainingEnv, IslandEnvironment validationEnv, IslandsSimulationSettings settings, RandomUtils random) {
        super(trainingEnv, validationEnv, settings, random);
    }

    @Override
    public IslandIndividual generateIndividual() {
        return new AverageIndividual(random.nextDouble());
    }

    @Override
    public Pair<IslandIndividual, IslandIndividual> mate(EvolutionContext evolutionContext, IslandIndividual parent1, IslandIndividual parent2) {

        double parent1Value = ((AverageIndividual) parent1).value;
        double parent2Value = ((AverageIndividual) parent2).value;

        double t = random.nextDouble();

        double child1Value = parent1Value * t + parent2Value * (1 - t);
        double child2Value = parent1Value * (1 - t) + parent2Value * t;

        return new Pair<>(new AverageIndividual(child1Value), new AverageIndividual(child2Value));
    }

    @Override
    public double evaluate(IslandIndividual individual, IslandEnvironment environment) {
        return - Math.abs(((AverageEnvironment) environment).getAverage() - ((AverageIndividual)individual).value);
    }
}
