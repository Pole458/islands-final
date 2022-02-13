import it.unipr.sowide.actodata.core.initialstructure.ActoDataStructure;
import it.unipr.sowide.islands.IslandEngine;
import it.unipr.sowide.islands.IslandEnvironment;
import it.unipr.sowide.islands.IslandsMaster;
import it.unipr.sowide.islands.settings.IslandsSimulationSettings;
import it.unipr.sowide.util.RandomUtils;

import java.io.IOException;

public class FunctionMinMaster extends IslandsMaster {
    public FunctionMinMaster(ActoDataStructure structure) {
        super(structure);
    }

    @Override
    protected String usageString() {
        return "usage to do";
    }

    @Override
    protected boolean checkNumberOfArguemnts(String[] argv) {
        return true;
    }

    @Override
    protected IslandEngine generateIslandEngine(IslandEnvironment trainingSet, IslandEnvironment validationData, IslandsSimulationSettings settings, RandomUtils random) {
        return new FunctionMinIslandEngine(trainingSet, validationEnv, settings, random);
    }

    public static void main(String[] args) throws IOException {
        FunctionMinMaster averageMaster = new FunctionMinMaster(new ActoDataStructure());
        averageMaster.setUp(args);
    }
}
