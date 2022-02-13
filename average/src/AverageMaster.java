import it.unipr.sowide.actodata.core.initialstructure.ActoDataStructure;
import it.unipr.sowide.islands.IslandEngine;
import it.unipr.sowide.islands.IslandEnvironment;
import it.unipr.sowide.islands.IslandsMaster;
import it.unipr.sowide.islands.settings.IslandsSimulationSettings;
import it.unipr.sowide.util.RandomUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class AverageMaster extends IslandsMaster {

    public AverageMaster(ActoDataStructure structure) {
        super(structure);
    }

    @Override
    protected String usageString() {
        // todo
        return "todo";
    }

    @Override
    protected boolean checkNumberOfArguemnts(String[] argv) {
        // todo
        return true;
    }

    @Override
    protected IslandEngine generateIslandEngine(IslandEnvironment trainingEnv, IslandEnvironment validationEnv, IslandsSimulationSettings settings, RandomUtils random) {
        return new AverageIslandEngine(trainingEnv, validationEnv, settings, random);
    }

    @Override
    protected void parseFileArgs(String[] argv) throws IOException {
        super.parseFileArgs(argv);
        for(int i = 0; i < argv.length; i++) {
            if ("-values".equals(argv[i])) {
                i++;
                if (i >= argv.length) unexpectedArguments();
                Collection<AverageEnvironment.AverageInstance> instances = parseValuesFromFile(new File(argv[i]));
                trainEnv = new AverageEnvironment(instances);
                validationEnv = new AverageEnvironment(instances);
                testEnv = new AverageEnvironment(instances);
            }
        }
    }

    public Collection<AverageEnvironment.AverageInstance> parseValuesFromFile(File file) throws IOException {
        List<AverageEnvironment.AverageInstance> values = new ArrayList<>();
        try (Scanner reader = new Scanner(new FileReader(file))) {
            while (reader.hasNextDouble()) values.add(new AverageEnvironment.AverageInstance(reader.nextDouble()));
        }
        return values;
    }

    public static void main(String[] args) throws IOException {
        AverageMaster averageMaster = new AverageMaster(new ActoDataStructure());
        averageMaster.setUp(args);
    }
}
