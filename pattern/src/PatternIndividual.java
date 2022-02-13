import it.unipr.sowide.gpj.core.EvolutionContext;
import it.unipr.sowide.islands.IslandIndividual;
import it.unipr.sowide.util.RandomUtils;

import java.util.Arrays;

public class PatternIndividual extends IslandIndividual {

    private final boolean[] values;

    public PatternIndividual(RandomUtils random) {
        values = new boolean[Pattern.SIZE];
        for(int i = 0; i < Pattern.SIZE; i++) {
            values[i] = random.nextBoolean();
        }
    }

    public PatternIndividual(boolean[] values) {
        this.values = values;
    }

    public boolean getValue(int index) {
        return values[index];
    }

    @Override
    public IslandIndividual copyIndividualWithoutMetaData() {
        return new PatternIndividual(values);
    }

    @Override
    public IslandIndividual mutate(EvolutionContext evolutionContext) {
        int index = evolutionContext.getRandom().randomInt(0, Pattern.SIZE);
        values[index] = !values[index];
        resetFitness();
        return this;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("\n");
        for(int y = 0; y < Pattern.HEIGHT; y++) {
            for(int x = 0;  x < Pattern.WIDTH; x++) {
                s.append(values[x + Pattern.WIDTH * y] ? "\u2588\u2588" : "  ");
            }
            s.append("\n");
        }
        return s.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PatternIndividual) {
            PatternIndividual that = (PatternIndividual) o;

            if(compareTo(that) == 0) return true;

            return Arrays.equals(values, that.values);
        }
        return super.equals(o);
    }
}
