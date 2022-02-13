package it.unipr.sowide.util;


import it.unipr.sowide.util.annotations.Namespace;

import java.io.File;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * A collection of utility static methods used to check requirements of
 * arguments of APIs when called from outside.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
//Note: it would be nice if these methods could pack some annotation contracts
// inside for some degree of static analysis and "effect inference"
@Namespace
public class Require {
    private Require() {
    }// do not instantiate

    /**
     * Requires a string to contain at least one non blank character.
     */
    public static void notBlankString(String s) {
        Require.notEmptyString(s);
        var notOK = IntStream.range(0, s.length())
                .noneMatch(i -> !Character.isWhitespace(s.charAt(i))
                        && !Character.isSpaceChar(s.charAt(i)));
        if (notOK) {
            throw new IllegalArgumentException("Expected non-blank string");
        }
    }

    /**
     * Requires a string to contain at least one character.
     */
    public static void notEmptyString(String s) {
        nonNull(s);
        if (s.isEmpty()) {
            throw new IllegalArgumentException("Expected non-empty string");
        }
    }

    /**
     * Requires {@code o} to be not null.
     */
    public static void nonNull(Object o) {
        Objects.requireNonNull(o);
    }

    /**
     * Requires all the {@code os} to be not null.
     */
    public static void nonNull(Object... os) {
        for (var o : os) {
            nonNull(o);
        }
    }

    /**
     * Requires an integer to be not null and strictly positive.
     *
     * @param valueName the name of the value in the exception message.
     */
    public static void strictlyPositive(Integer i, String valueName) {
        nonNull(i);
        if (i <= 0) {
            throw new IllegalArgumentException(
                    "Expected strictly positive integer " +
                            (valueName != null ? ": " + valueName : ""));
        }
    }

    /**
     * Requires an integer to be not null and strictly positive.
     */
    public static void strictlyPositive(Integer i) {
        strictlyPositive(i, null);
    }

    /**
     * Requires the integer to be not null and strictly negative.
     *
     * @param valueName the name of the value in the exception message.
     */
    public static void strictlyNegative(Integer i, String valueName) {
        nonNull(i);
        if (i >= 0) {
            throw new IllegalArgumentException(
                    "Expected strictly negative integer " +
                            (valueName != null ? ": " + valueName : ""));
        }
    }

    /**
     * Requires the integer to be not null and strictly negative.
     */
    public static void strictlyNegative(Integer i) {
        strictlyNegative(i, null);
    }

    /**
     * Requires the integer to be not null and non negative.
     *
     * @param valueName the name of the value in the exception message.
     */
    public static void nonNegative(Integer i, String valueName) {
        nonNull(i);
        if (i < 0) {
            throw new IllegalArgumentException(
                    "Expected non-negative integer " +
                            (valueName != null ? ": " + valueName : ""));
        }
    }

    /**
     * Requires the integer to be not null and non negative.
     */
    public static void nonNegative(Integer i) {
        nonNegative(i, null);
    }


    /**
     * Requires the integer to be not null and non positive.
     *
     * @param valueName the name of the value in the exception message.
     */
    public static void nonPositive(Integer i, String valueName) {
        nonNull(i);
        if (i > 0) {
            throw new IllegalArgumentException(
                    "Expected non-positive integer " +
                            (valueName != null ? ": " + valueName : ""));
        }
    }


    /**
     * Requires {@code e} to be in the collection {@code c}.
     *
     * @param c   the collection
     * @param e   the element
     * @param <T> the type of the element
     */
    public static <T> void contains(Collection<? super T> c, T e) {
        nonNull(c, e);
        if (!c.contains(e)) {
            throw new IllegalStateException("The collection " + c +
                    " expected to contain " + e);
        }
    }

    /**
     * Requires the {@code file} to be readable.
     */
    public static void readable(File file) {
        nonNull(file);
        if (!file.canRead()) {
            throw new IllegalStateException("File '" + file +
                    "' is not readable");
        }
    }

    /**
     * Requires {@code value} to be between {@code l} and {@code r}.
     *
     * @param l     lower bound
     * @param r     upper bound
     * @param value the value to check
     * @throws IllegalArgumentException if the value is not in range or if
     *                                  r < l.
     */
    public static void inRange(double l, double r, double value) {
        if (r < l) {
            throw new IllegalArgumentException("Invalid range: [" + l +
                    " to " + r + "]");
        }
        if (value < l || r < value) {
            throw new IllegalArgumentException(
                    "Invalid value, should be in range " +
                            "[" + l + " to " + r + "] but was: " + value);
        }
    }

    /**
     * Requires the collection to be notEmpty.
     *
     * @param collection the collection
     */
    public static void notEmpty(Collection<?> collection) {
        if (collection.isEmpty()) {
            throw new IllegalArgumentException(
                    "Received empty collection when required non-empty");
        }
    }

    /**
     * Requirs the {@code value}  to be a valid value in the {@code enu} enum.
     *
     * @param enu       the enum class
     * @param value     the value to check
     * @param valueName the name of the value in the exception message.
     * @param <E>       the type of the enum
     */
    public static <E extends Enum<E>> void
    inEnum(Class<E> enu, String value, String valueName) {
        try {
            E.valueOf(enu, value);
        } catch (IllegalArgumentException ignored) {
            throw new IllegalArgumentException(
                    "Invalid value, should be part of enum '" +
                            enu.getName() + "'" +
                            (valueName != null ? ": " + valueName : ""));
        }
    }
}
