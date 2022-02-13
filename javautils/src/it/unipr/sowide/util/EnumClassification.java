package it.unipr.sowide.util;

import it.unipr.sowide.util.annotations.Mutable;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * An auxiliary abstract data type to associate the result of a classification
 * with a set of classes in the form of an enum.
 *
 * @param <E> the enum type
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
@Mutable
public class EnumClassification<E extends Enum<E>> implements Serializable {
    /**
     * A {@link Map} that associates each class in the enum with the degree of
     * confidence that the instance belongs to that class.
     */
    private Map<E, Double> classificationDegrees = new HashMap<>();

    /**
     * Creates a classification for the specified {@code enumType}, where the
     * degree of confidence for each class is 0.0, except for the
     * {@code assigned} one.
     *
     * @param enumType the enum types with the multinomial classes
     * @param assigned the chosen assigned class
     */
    public EnumClassification(Class<? extends E> enumType, E assigned) {
        for (E e : enumType.getEnumConstants()) {
            classificationDegrees.put(e, e == assigned ? 1.0 : 0.0);
        }
    }

    /**
     * Creates a classification for the specified {@code enumType}, and the
     * degrees of confidence are extracted from {@code values}, in order.
     * <p>
     * Only {@code min(E, V)} assignments are done, where E is the number of
     * values of the enum E and V is the size of {@code values}
     *
     * @param enumType the enum type
     * @param values   the degrees of confidence to be assigned
     */
    public EnumClassification(
            Class<? extends E> enumType,
            List<Double> values
    ) {
        E[] enumConstants = enumType.getEnumConstants();
        int howManyAssignments = Math.min(values.size(), enumConstants.length);
        int i = 0;
        for (; i < howManyAssignments; i++) {
            classificationDegrees.put(enumConstants[i], values.get(i));
        }

        if (i < enumConstants.length) {
            for (; i < enumConstants.length; i++) {
                classificationDegrees.put(enumConstants[i], 0.0);
            }
        }
    }

    /**
     * Creates a classification for the specified {@code enumType} with the
     * values in {@code values} as confidence degrees.
     * <p>
     * Only {@code min(E, V)} assignments are done, where E is the number of
     * values of the enum E and V is the size of {@code values}
     *
     * @param enumType the enum type
     * @param values   the confidence degrees
     */
    public EnumClassification(Class<? extends E> enumType, double[] values) {
        this(
                enumType,
                Arrays.stream(values).boxed().collect(Collectors.toList())
        );
    }

    /**
     * Creates a classification for the {@code enumType} where all confidence
     * degrees are zero.
     *
     * @param enumType the enum type
     */
    public EnumClassification(Class<? extends E> enumType) {
        for (E e : enumType.getEnumConstants()) {
            classificationDegrees.put(e, 0.0);
        }
    }

    /**
     * Returns the confidence degree of the specified {@code class_} (i.e. how
     * much the classificator thinks that the classified instance belongs to
     * that class).
     *
     * @param class_ the class
     * @return the confidence degree
     */
    public double getDegree(E class_) {
        return classificationDegrees.getOrDefault(class_, 0.0);
    }

    /**
     * Sets the confidence degree of a specified class to the specified value.
     *
     * @param class_ the class
     * @param value  the value
     */
    public void setDegree(E class_, double value) {
        classificationDegrees.put(class_, value);

    }

    /**
     * Returns the class with most confidence degree value, according to the
     * provided {@code comparator}.
     *
     * @param comparator the comparator that compares the confidence values
     * @return the class enum value with "maximum" (according to the comparator)
     * confidence value.
     */
    public Optional<E> getTopClass(Comparator<Double> comparator) {
        return classificationDegrees.entrySet().stream()
                .max((entry1, entry2) -> comparator.compare(
                        entry1.getValue(),
                        entry2.getValue()
                ))
                .map(Map.Entry::getKey);
    }

    /**
     * Returns the class with most confidence degree value, i.e. the class with
     * the highest double precision floating point value.
     * <p></p>
     * This is equivalent to calling {@link #getTopClass(Comparator)} with
     * {@link Double#compareTo(Double)} as comparator.
     *
     * @return the class with most confidence degree value.
     */
    public Optional<E> getTopClass() {
        return getTopClass(Double::compareTo);
    }

    /**
     * Normalizes all the confidence degrees values, so they are all between
     * {@code 0.0} and {@code 1.0}.
     */
    public void normalize() {
        var sum = classificationDegrees.values().stream()
                .mapToDouble(x -> x)
                .sum();
        classificationDegrees = classificationDegrees.entrySet().stream()
                .map(entry -> Map.entry(
                        entry.getKey(),
                        entry.getValue() / sum
                ))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public String toString() {
        return "EnumClassification{" +
                "classificationDegrees=" + classificationDegrees +
                '}';
    }
}
