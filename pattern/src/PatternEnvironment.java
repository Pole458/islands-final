import it.unipr.sowide.islands.IslandEnvironment;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

public class PatternEnvironment extends IslandEnvironment {

    public static class PatternInstance extends Instance {
        int index;
        boolean value;

        PatternInstance(int index, boolean value) {
             this.index = index;
             this.value = value;
        }

        @Override
        public int compareTo(Instance o) {
            return Integer.compare(index, ((PatternInstance)o).index);
        }
    }

    public PatternEnvironment() { }

    public PatternEnvironment(Collection<PatternInstance> instances) {
        this.instances.addAll(instances);
    }

    @Override
    protected IslandEnvironment createEnvironment() {
        return new PatternEnvironment();
    }

    public Optional<Boolean> getValue(int index) {
        for (Instance value : instances) {
            PatternInstance instance = (PatternInstance) value;
            if (index == instance.index) return Optional.of(instance.value);
        }
        return Optional.empty();
    }

    public Iterator<PatternInstance> iterator() {
        return instances.stream().map(instance -> (PatternInstance)instance).iterator();
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();

        s.append("Instances: ").append(instances.size()).append("\n");

        for(int y = 0; y < Pattern.HEIGHT; y++) {
            for(int x = 0;  x < Pattern.WIDTH; x++) {
                Optional<Boolean> value = getValue(x + Pattern.WIDTH * y);
                s.append(value.isEmpty() ? "??" : value.get() ? "\u2588\u2588" : "  ");
            }
            s.append("\n");
        }

        return s.toString();
    }
}
