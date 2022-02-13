package it.unipr.sowide.util;

import it.unipr.sowide.util.annotations.Mutable;

import java.util.List;
import java.util.Random;

/**
 * A wrapper around {@link Random} that adds some utilities.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
@Mutable
public class RandomUtils {
    private final Random random;

    /**
     * Creates a new random generator with random seed.
     */
    public RandomUtils() {
        this.random = new Random();
    }

    /**
     * Creates a new random generator using the specified {@code random} as
     * internal random generator.
     *
     * @param random the {@link Random} used as internal generator.
     */
    public RandomUtils(Random random) {
        this.random = random;
    }

    /**
     * Creates a new random generator using the specified {@code seed}.
     *
     * @param seed the seed
     */
    public RandomUtils(long seed) {
        this.random = new Random(seed);
    }

    /**
     * Returns a random element from the list, or null if the list is empty.
     *
     * @param list the input list
     * @param <T>  the type of the elements of the list
     * @return a random element from the list, or null if the list is empty.
     */
    public <T> T pick(List<T> list) {
        if (list.isEmpty()) {
            return null;
        }
        return list.get(random.nextInt(list.size()));
    }

    /**
     * Returns true with the given probability ({@code value} between 0.0 and
     * 1.0).
     *
     * @param value the probability
     * @return true with the given probability ({@code value} between 0.0 and
     * 1.0).
     */
    public boolean probabilityOutcome(double value) {
        Require.inRange(0.0, 1.0, value);
        return random.nextDouble() < value;
    }

    /**
     * Picks an element from the list, where the probability for each element
     * to be picked is given by the element at the same index in
     * {@code weights}.
     *
     * @param list    a list of elements
     * @param weights a list of same size of {@code list}, with the
     *                probabilities for each element to be picked
     * @param <T>     the type of the element
     * @return the picked element, or null if {@code list} is empty.
     * @throws IllegalArgumentException if the size of {@code list} and
     *                                  {@code weights} is not the same.
     */
    public <T> T weightedPick(List<T> list, List<Double> weights) {
        if (list.isEmpty()) {
            return null;
        }

        if (list.size() != weights.size()) {
            throw new IllegalArgumentException(
                    "The number of candidates must be equal " +
                            "to the number of associated weights");
        }


        double rand = random.nextDouble() * weights.stream()
                .mapToDouble(i -> i)
                .sum();
        double sum = weights.get(0);
        T candidate = list.get(0);

        for (int i = 1; i < list.size(); i++) {
            if (rand < sum) {
                return candidate;
            }
            sum += weights.get(i);
            candidate = list.get(i);
        }

        return candidate;
    }

    /**
     * Returns a random integer between min, inclusive and max, exclusive.
     *
     * @param min the minimum value that could be generated
     * @param max the strict upper bound on the values that can be generated
     * @return a random int in range.
     */
    public int randomInt(int min, int max) {
        if (max - min <= 0) {
            throw new IllegalArgumentException(
                    "max must be > of min, instead: " + max + " <= " + min);
        }
        return random.nextInt(max - min) + min;
    }

    /**
     * @return a new random generator object using a random long from this
     * generator as seed.
     */
    public RandomUtils childRandom() {
        return new RandomUtils(random.nextLong());
    }

    /**
     * @return a random double between 0.0 and 1.0.
     * @see Random#nextDouble()
     */
    public double nextDouble() {
        return random.nextDouble();
    }

    /**
     * @return a random int.
     * @see Random#nextInt()
     */
    public int nextInt() {
        return random.nextInt();
    }

    /**
     * @return a random int.
     * @see Random#nextInt(int)
     */
    public int nextInt(int bound) {
        return random.nextInt(bound);
    }

    /**
     * @return a random boolean.
     * @see Random#nextBoolean()
     */
    public boolean nextBoolean() {
        return random.nextBoolean();
    }

    /**
     * @return a random long.
     * @see Random#nextLong()
     */
    public long nextLong() {
        return random.nextLong();
    }

    /**
     * @return the wrapped {@link Random}
     */
    public Random getJavaRandom() {
        return this.random;
    }
}
