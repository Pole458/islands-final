import it.unipr.sowide.gpj.core.EvolutionContext;
import it.unipr.sowide.islands.IslandIndividual;

public class AverageIndividual extends IslandIndividual {

    public final double value;

    public AverageIndividual(double value) {
        this.value = value;
    }

    @Override
    public IslandIndividual copyIndividualWithoutMetaData() {
        return new AverageIndividual(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public IslandIndividual mutate(EvolutionContext evolutionContext) {
        return new AverageIndividual(Math.max(Math.min(value + (evolutionContext.getRandom().nextDouble() - 0.5) / 10, 1),0));
    }
}
