package it.unipr.sowide.islands;

import it.unipr.sowide.util.RandomUtils;

import java.util.*;

public abstract class IslandEnvironment {

    public abstract static class Instance implements Comparable<Instance> { }

    protected final SortedSet<Instance> instances = new TreeSet<>();

    protected abstract IslandEnvironment createEnvironment();

    List<IslandEnvironment> startingSplitContiguous(int numIslands) {
        // Create environments
        List<IslandEnvironment> environments = new ArrayList<>(numIslands);
        for(int i = 0; i < numIslands; i++) environments.add(createEnvironment());

        // Sorted instances
        List<Instance> sortedInstances = new ArrayList<>(instances);

        // Contiguous split
        int splitSize = instances.size() / numIslands;
        for(int i = 0; i < sortedInstances.size(); i++) environments.get(i / splitSize).instances.add(sortedInstances.get(i));

        return environments;
    }

    List<IslandEnvironment> startingSplitRandom(int numIslands, RandomUtils random) {
        // Create environments
        List<IslandEnvironment> environments = new ArrayList<>(numIslands);
        for(int i = 0; i < numIslands; i++) environments.add(createEnvironment());

        // Shuffle instances
        List<Instance> shuffledInstances = new ArrayList<>(instances);
        Collections.shuffle(shuffledInstances, random.getJavaRandom());

        // Random split
        int splitSize = instances.size() / numIslands;
        for(int i = 0; i < shuffledInstances.size(); i++) {
            environments.get(i / splitSize).instances.add(shuffledInstances.get(i));
        }

        return environments;
    }

    List<IslandEnvironment> startingSplitRoundRobin(int numIslands) {
        // Create environments
        List<IslandEnvironment> environments = new ArrayList<>(numIslands);
        for(int i = 0; i < numIslands; i++) environments.add(createEnvironment());

        // Sorted instances
        List<Instance> sortedInstances = new ArrayList<>(instances);

        // Round-Robin split
        for(int i = 0; i < sortedInstances.size(); i++) environments.get(i % numIslands).instances.add(sortedInstances.get(i));

        return environments;
    }

    Collection<Instance> randomSplit(RandomUtils randomUtils, int instancesNumber) {
        // Shuffled instances
        List<Instance> shuffledInstances = new ArrayList<>(instances);
        Collections.shuffle(shuffledInstances, randomUtils.getJavaRandom());

        // Create split instances
        List<Instance> splitInstances = new ArrayList<>();

        // Try to take n instances
        for(int i = 0; i < instancesNumber && i < shuffledInstances.size(); i++) {
            Instance instance = shuffledInstances.get(i);
            instances.remove(instance);
            splitInstances.add(instance);
        }

        return splitInstances;
    }

    Collection<Instance> contiguousSplit(RandomUtils randomUtils, int instancesNumber) {
        // Sorted instances
        List<Instance> sortedInstances = new ArrayList<>(instances);

        // Create split instances
        List<Instance> splitInstances = new ArrayList<>();

        // Try to take n instances
        for(int i = 0, startingIndex = randomUtils.randomInt(0, instances.size()); i < instancesNumber && i < sortedInstances.size(); i++) {
            Instance instance = sortedInstances.get((i + startingIndex) % instances.size());
            instances.remove(instance);
            splitInstances.add(instance);
        }

        return splitInstances;
    }

    @Override
    public abstract String toString();
}
