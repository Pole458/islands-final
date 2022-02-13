import it.unipr.sowide.gpj.core.EvolutionContext;
import it.unipr.sowide.islands.IslandEngine;
import it.unipr.sowide.islands.IslandEnvironment;
import it.unipr.sowide.islands.IslandIndividual;
import it.unipr.sowide.islands.settings.IslandsSimulationSettings;
import it.unipr.sowide.util.Pair;
import it.unipr.sowide.util.RandomUtils;

public class FunctionMinIslandEngine extends IslandEngine {
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
    public FunctionMinIslandEngine(IslandEnvironment trainingEnv, IslandEnvironment validationEnv, IslandsSimulationSettings settings, RandomUtils random) {
        super(trainingEnv, validationEnv, settings, random);
    }

    @Override
    public IslandIndividual generateIndividual() {
        return new FunctionMinIndividual(random);
    }

    @Override
    public Pair<IslandIndividual, IslandIndividual> mate(EvolutionContext evolutionContext, IslandIndividual parent1, IslandIndividual parent2) {
        double[] parent1Values = ((FunctionMinIndividual) parent1).values;
        double[] parent2Values = ((FunctionMinIndividual) parent2).values;

        double[] child1Values = new double[Function.VARIABLES];
        double[] child2Values = new double[Function.VARIABLES];

        for(int i = 0; i < Function.VARIABLES; i++) {
            if(random.nextBoolean()) {
                child1Values[i] = parent1Values[i];
                child2Values[i] = parent2Values[i];
            } else {
                child1Values[i] = parent2Values[i];
                child2Values[i] = parent1Values[i];
            }
        }

        return new Pair<>(new FunctionMinIndividual(child1Values), new FunctionMinIndividual(child2Values));
    }

    @Override
    public double evaluate(IslandIndividual individual, IslandEnvironment environment) {
        FunctionMinIndividual ind = (FunctionMinIndividual) individual;
        return Function.function(ind.values);
    }
}
