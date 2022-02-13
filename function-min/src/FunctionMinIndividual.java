import it.unipr.sowide.gpj.core.EvolutionContext;
import it.unipr.sowide.islands.IslandIndividual;
import it.unipr.sowide.util.RandomUtils;

public class FunctionMinIndividual extends IslandIndividual {

    public double[] values = new double[Function.VARIABLES];

    public FunctionMinIndividual(RandomUtils random) {
        for (int i = 0; i < Function.VARIABLES; i++) values[i] = random.nextDouble() * Function.RANGE + Function.MIN;
    }

    public FunctionMinIndividual(double[] values) {
        System.arraycopy(values, 0, this.values, 0, Function.VARIABLES);
    }

    @Override
    public IslandIndividual copyIndividualWithoutMetaData() {
        return new FunctionMinIndividual(values);
    }

    @Override
    public IslandIndividual mutate(EvolutionContext evolutionContext) {
        RandomUtils random = evolutionContext.getRandom();
        int index = random.nextInt(Function.VARIABLES);
        values[index] = Function.clamp(
                values[index] + (random.nextDouble() - 0.5) * Function.RANGE * Function.MUTATION_SIZE
        );
        resetFitness();
        return this;
    }

    @Override
    public String toString() {
        return "function min individual .toString todo";
    }
}
