package it.unipr.sowide.gpj.smcgp64;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

/**
 * A collection of data instances in the form of a tuple of 2 64-bit values.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class SMCGP64DataSet {

    public final static String numberPattern = "\\d+";
    private final List<SMCGP64Instance> instances = new ArrayList<>();
    private final HashSet<SMCGP64Instance> instanceHashSet = new HashSet<>();
    private final int[] instancesCounts = new int[SMCGP64.N_CLASSES];

    /**
     * Inserts a new instance in the dataset using the two patterns and the
     * specified class.
     */
    public void put(long pat1, long pat2, int clas) {
        SMCGP64Instance instance = new SMCGP64Instance(clas, pat1, pat2);
        putInstance(instance);
    }

    private void putInstance(SMCGP64Instance instance) {
        if (!instanceHashSet.contains(instance)) {
            instances.add(instance);
            instanceHashSet.add(instance);
            instancesCounts[instance.getClas()]++;
        }
    }

    /**
     * @return the number of instances in the dataset
     */
    public int size() {
        return instances.size();
    }

    public long getPattern1(int instance) {
        return instances.get(instance).getPat1();
    }

    public long getPattern2(int instance) {
        return instances.get(instance).getPat2();
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

    public SMCGP64Instance getInstance(int index) {
        return instances.get(index);
    }

    private void parseInstance(Scanner scanLine) {

        String p1 = scanLine.next(numberPattern);
        String p2 = scanLine.next(numberPattern);
        int clas = scanLine.nextInt();

        long ip1 = Long.parseUnsignedLong(p1);
        long ip2 = Long.parseUnsignedLong(p2);

        this.put(ip1, ip2, clas);
    }

    public void parseInstancesFromFile(File file) {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
