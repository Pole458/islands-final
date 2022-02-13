import it.unipr.sowide.gpj.core.EvolutionContext;
import it.unipr.sowide.islands.IslandEngine;
import it.unipr.sowide.islands.IslandEnvironment;
import it.unipr.sowide.islands.IslandIndividual;
import it.unipr.sowide.islands.settings.IslandsSimulationSettings;
import it.unipr.sowide.util.Pair;
import it.unipr.sowide.util.RandomUtils;

import java.util.Iterator;

public class PatternIslandEngine extends IslandEngine {

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
    public PatternIslandEngine(IslandEnvironment trainingEnv, IslandEnvironment validationEnv, IslandsSimulationSettings settings, RandomUtils random) {
        super(trainingEnv, validationEnv, settings, random);
    }

    @Override
    public PatternIndividual generateIndividual() {
        return new PatternIndividual(random);
    }

    @Override
    public Pair<IslandIndividual, IslandIndividual> mate(EvolutionContext evolutionContext, IslandIndividual parent1, IslandIndividual parent2) {

        PatternIndividual patterParent1 = (PatternIndividual) parent1;
        PatternIndividual patterParent2 = (PatternIndividual) parent2;

        boolean[] child1values = new boolean[Pattern.SIZE];
        boolean[] child2values = new boolean[Pattern.SIZE];

        int point1 = random.randomInt(1, Pattern.SIZE - 2);
        int point2 = random.randomInt(point1 + 1, Pattern.SIZE - 1);

        for(int i = 0; i < point1; i++) {
            child1values[i] = patterParent1.getValue(i);
            child2values[i] = patterParent2.getValue(i);
        }

        for(int i = point1; i < point2; i++) {
            child1values[i] = patterParent2.getValue(i);
            child2values[i] = patterParent1.getValue(i);
        }

        for(int i = point2; i < Pattern.SIZE; i++) {
            child1values[i] = patterParent1.getValue(i);
            child2values[i] = patterParent2.getValue(i);
        }

        return new Pair<>(new PatternIndividual(child1values), new PatternIndividual(child2values));
    }

    @Override
    public double evaluate(IslandIndividual individual, IslandEnvironment environment) {

        PatternEnvironment patternEnvironment = (PatternEnvironment) environment;
        PatternIndividual patternIndividual = (PatternIndividual) individual;

        int correct = 0;
        for (Iterator<PatternEnvironment.PatternInstance> it = patternEnvironment.iterator(); it.hasNext(); ) {
            PatternEnvironment.PatternInstance instance = it.next();
            if(instance.value == patternIndividual.getValue(instance.index)) {
                correct++;
            }
        }

        return correct / (double) Pattern.SIZE;
    }
}
