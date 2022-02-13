import it.unipr.sowide.islands.IslandEnvironment;

public class FunctionMinEnvironment extends IslandEnvironment {

    public FunctionMinEnvironment() {}

    @Override
    protected IslandEnvironment createEnvironment() {
        return new FunctionMinEnvironment();
    }

    @Override
    public String toString() {
        return "fnctio min env tostring todo";
    }
}
