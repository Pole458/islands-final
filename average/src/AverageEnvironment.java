import it.unipr.sowide.islands.IslandEnvironment;

import java.util.Collection;

public class AverageEnvironment extends IslandEnvironment {

    public static class AverageInstance extends Instance {
        double value;
        AverageInstance(double value){ this.value = value; }

        @Override
        public int compareTo(Instance o) {
            return Double.compare(value, ((AverageInstance)o).value);
        }
    }

    public AverageEnvironment() { }

    public AverageEnvironment(Collection<AverageInstance> instances) {
        this.instances.addAll(instances);
    }

    public double getAverage() {
        return instances.stream().mapToDouble(instance -> ((AverageInstance)instance).value).average().orElse(0.0);
    }

    protected IslandEnvironment createEnvironment() {
        return new AverageEnvironment();
    }

    @Override
    public String toString() {
        return "Environment: Average: " + getAverage() + ", instances: " + instances.size();
    }
}
