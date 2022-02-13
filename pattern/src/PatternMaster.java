import it.unipr.sowide.actodata.core.initialstructure.ActoDataStructure;
import it.unipr.sowide.islands.IslandEngine;
import it.unipr.sowide.islands.IslandEnvironment;
import it.unipr.sowide.islands.IslandsMaster;
import it.unipr.sowide.islands.settings.IslandsSimulationSettings;
import it.unipr.sowide.util.RandomUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

public class PatternMaster extends IslandsMaster {

    public PatternMaster(ActoDataStructure structure) {
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
    protected IslandEngine generateIslandEngine(IslandEnvironment trainingSet, IslandEnvironment validationData, IslandsSimulationSettings settings, RandomUtils random) {
        return new PatternIslandEngine(trainingSet, validationData, settings, random);
    }

    @Override
    protected void parseFileArgs(String[] argv) throws IOException {
        super.parseFileArgs(argv);
        for(int i = 0; i < argv.length; i++) {
            if ("-pattern".equals(argv[i])) {
                i++;
                if (i >= argv.length) unexpectedArguments();
                Collection<PatternEnvironment.PatternInstance> instances = parseValuesFromFile(new File(argv[i]));
                trainEnv = new PatternEnvironment(instances);
                validationEnv = new PatternEnvironment(instances);
                testEnv = new PatternEnvironment(instances);
            }
        }
    }

    public Collection<PatternEnvironment.PatternInstance> parseValuesFromFile(File file) throws IOException {
        Scanner reader = new Scanner(new FileReader(file));

        Pattern.HEIGHT = reader.nextInt();
        Pattern.WIDTH = reader.nextInt();
        Pattern.SIZE = Pattern.WIDTH * Pattern.HEIGHT;

        List<PatternEnvironment.PatternInstance> values = new ArrayList<>();
        for(int i = 0; i < Pattern.SIZE; i++)
            values.add(new PatternEnvironment.PatternInstance(i, reader.nextInt() > 0));
        return values;
    }

    public static void main(String[] argv) throws IOException {
        PatternMaster master = new PatternMaster(new ActoDataStructure());
        master.setUp(argv);
    }
}
