package it.unipr.sowide.gpj.smcgp32;

import it.unipr.sowide.util.RandomUtils;
import it.unipr.sowide.util.Require;

import java.io.*;
import java.util.*;

/**
 * A collection of data instances in the form of a tuple of 4 32-bit values.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class SMCGP32DataSet {

    public final static String numberPattern = "\\d+";
    private final List<SMCGP32Instance> instances = new ArrayList<>();
    private final HashSet<SMCGP32Instance> instanceHashSet = new HashSet<>();
    private final int[] instancesCounts = new int[SMCGP32.N_CLASSES];

    /**
     * Inserts a new instance in the dataset using the four patterns and the
     * specified class.
     */
    public void put(int pat1, int pat2, int pat3, int pat4, int clas) {
        SMCGP32Instance instance = new SMCGP32Instance(
                clas,
                pat1,
                pat2,
                pat3,
                pat4
        );
        putInstance(instance);
    }

    /**
     * @return the number of instances in the dataset
     */
    public int size() {
        return instances.size();
    }


    public int getPattern1(int instance) {
        return instances.get(instance).getPat1();
    }

    public int getPattern2(int instance) {
        return instances.get(instance).getPat2();
    }

    public int getPattern3(int instance) {
        return instances.get(instance).getPat3();
    }

    public int getPattern4(int instance) {
        return instances.get(instance).getPat4();
    }

    public int getClass(int instance) {
        return instances.get(instance).getClas();
    }

    /**
     * Returns how many instances for the specified class are in the dataset
     *
     * @param clas the class
     * @return the count of instances for the class
     */
    public int getCaseCountForClass(int clas) {
        return instancesCounts[clas];
    }



    private void parseInstance(Scanner scanLine) {

        String p1 = scanLine.next(numberPattern);
        String p2 = scanLine.next(numberPattern);
        String p3 = scanLine.next(numberPattern);
        String p4 = scanLine.next(numberPattern);
        int clas = scanLine.nextInt();

        int ip1 = Integer.parseUnsignedInt(p1);
        int ip2 = Integer.parseUnsignedInt(p2);
        int ip3 = Integer.parseUnsignedInt(p3);
        int ip4 = Integer.parseUnsignedInt(p4);

        this.put(ip1, ip2, ip3, ip4, clas);

    }

    public SMCGP32Instance getInstance(int index) {
        return instances.get(index);
    }

    public void putInstance(SMCGP32Instance instance) {
        if (!instanceHashSet.contains(instance)) {
            instances.add(instance);
            instanceHashSet.add(instance);
            instancesCounts[instance.getClas()]++;
        }
    }

    public void parseInstancesFromFile(File file) throws IOException {
        try (Scanner reader = new Scanner(new FileReader(file))) {
            String countLine = reader.nextLine();

            while (reader.hasNextLine() || reader.hasNext(numberPattern)) {
                if (reader.hasNextLine()) {
                    reader.nextLine();
                }
                if (reader.hasNext(numberPattern)) {
                    this.parseInstance(reader);
                }
            }
        }
    }

    public void dumpToFile(File file) {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(file))) {
            w.write(instances.size() + "\n");
            for (SMCGP32Instance instance : instances) {
                w.write(Integer.toUnsignedString(instance.getPat1()) + " ");
                w.write(Integer.toUnsignedString(instance.getPat2()) + " ");
                w.write(Integer.toUnsignedString(instance.getPat3()) + " ");
                w.write(Integer.toUnsignedString(instance.getPat4()) + " ");
                w.write("" + instance.getClas());
                w.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void dumpToSet(HashSet<SMCGP32Instance> data) {
        data.addAll(instanceHashSet);
    }

    public List<SMCGP32DataSet> randomSplit(int parts, RandomUtils random) {
        Require.nonNull(random);
        Require.strictlyPositive(parts);
        List<SMCGP32Instance> instances = new ArrayList<>(this.instances);
        Collections.shuffle(instances, random.getJavaRandom());
        List<SMCGP32DataSet> result = new ArrayList<>();
        int part = instances.size() / parts;
        int rest = instances.size() % parts; // the first gets the rest
        for (int i = 0; i < parts; i++) {
            var subDataset = new SMCGP32DataSet();
            if (i == 0) {
                for (int j = 0; j < rest; j++) {
                    subDataset.putInstance(instances.remove(0));
                }
            }
            for (int j = 0; j < part; j++) {
                subDataset.putInstance(instances.remove(0));
            }
            result.add(subDataset);
        }
        return result;
    }
}
