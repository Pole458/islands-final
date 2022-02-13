package it.unipr.sowide.util;

import it.unipr.sowide.util.annotations.Immutable;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A CheckedCast is an immutable type representing a runtime cast operation of
 * a value towards a specific type.
 * <p></p>
 * It is inspired in functionality by {@link Optional} but around the concept of
 * "safe type cast" instead of nullability.
 * <p></p>
 * Note that due to erasure problems, the users of this class should use the
 * operations in this class very carefully when {@code T} is a parameterized
 * type.
 *
 * @param <T> the target type of the cast.
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
@Immutable
public class CheckedCast<T> {
    private final T value;

    private CheckedCast(T value) {
        this.value = value;
    }

    /**
     * Creates an {@link Optional} that contains {@code o} casted to {@link T}
     * if the cast is successful, and {@link Optional#empty()} otherwise.
     * <p></p>
     * Note that the type check and the casting operations are performed via
     * Java Reflection using {@code type}.
     *
     * @param type the class of the type to which {@code o} will be casted.
     * @param o    the object to be cast.
     * @param <T>  the target type
     * @return an {@link Optional} that contains {@code o} casted to {@link T}
     * if the cast is successful, and {@link Optional#empty()} otherwise.
     */
    public static <T> Optional<T> cast(Class<? extends T> type, Object o) {
        if (type.isInstance(o)) {
            return Optional.of(type.cast(o));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Creates a {@link CheckedCast} that contains {@code o} casted to {@link T}
     * if the cast is succesful, and {@link CheckedCast#empty()} otherwise.
     *
     * @param typedNull any value of type T should do, just pass
     *                  {@code (T) null}. The value is ignored, this is only
     *                  useful to resolve correctly the generic type parameter
     *                  of the method.
     * @param o         the value to be casted to {@link T}
     * @param <T>       the target type
     * @return a {@link CheckedCast} that can be used for further computations.
     */
    public static <T> CheckedCast<T> cast(T typedNull, Object o) {
        try {
            @SuppressWarnings("unchecked")
            T t = (T) o;
            return CheckedCast.of(t);
        } catch (ClassCastException ignored) {
            return CheckedCast.empty();
        }
    }

    /**
     * Creates a {@link CheckedCast} that contains {@code o} casted to {@link T}
     * if the cast is succesful, and {@link CheckedCast#empty()} otherwise.
     *
     * @param o   the value to be casted to {@link T}
     * @param <T> the target type
     * @return a {@link CheckedCast} that can be used for further computations.
     */
    public static <T> CheckedCast<T> cast(Object o) {
        return cast((T) null, o);
    }

    private static <T> CheckedCast<T> of(T value) {
        return new CheckedCast<>(value);
    }

    private static <T> CheckedCast<T> empty() {
        return new CheckedCast<>(null);
    }

    /**
     * Returns the value if the cast was successful. Throws otherwise.
     *
     * @return the value
     * @throws NoSuchElementException if the cast was not successful.
     */
    public T get() {
        if (value == null) {
            throw new NoSuchElementException("No value present");
        }
        return value;
    }

    /**
     * Method that can be used to test if the cast was successful.
     *
     * @param emptyConsumer any consumer that does nothing will do, for example
     *                      {@code (__)->{} }. Internally used to perform the
     *                      check.
     * @return true if the cast was successful, false otherwise.
     */
    public boolean test(Consumer<T> emptyConsumer) {
        if (value == null) {
            return false;
        }
        try {
            emptyConsumer.accept(value);
            return true;
        } catch (ClassCastException ignored) {
            return false;
        }
    }

    /**
     * If the cast is successful, it performs the {@code action}. Otherwise, it
     * does nothing.
     *
     * @param action the action to be performed if the cast was successful.
     */
    public void ifSuccessful(Consumer<? super T> action) {
        if (value != null) {
            try {
                action.accept(value);
            } catch (ClassCastException ignored) {
            }
        }
    }

    /**
     * If the cast is successful, it performs the {@code action}. Otherwise, it
     * calls {@code orElse}.
     *
     * @param action the action to be performed if the cast was successful.
     * @param orElse the action to be performed if the cast was not successful.
     */
    public void ifSuccessfulOrElse(Consumer<? super T> action, Runnable orElse) {
        if (value != null) {
            try {
                action.accept(value);
            } catch (ClassCastException ignored) {
                orElse.run();
            }
        } else {
            orElse.run();
        }
    }


    /**
     * If the cast is successful, checks the wrapped value against the
     * {@code predicate}; if it returns false, the returned {@link CheckedCast}
     * will be unsuccessful.
     *
     * @param predicate the predicate the value is checked against.
     * @return a new {@link CheckedCast}: if the cast is successful, checks the
     * wrapped value against the {@code predicate}; if it returns false, the
     * returned {@link CheckedCast} will be unsuccessful.
     */
    public CheckedCast<T> filter(Predicate<? super T> predicate) {
        Require.nonNull(predicate);
        if (value == null) {
            return empty();
        } else {
            try {
                return predicate.test(value) ? this : empty();
            } catch (ClassCastException ignored) {
                return empty();
            }
        }
    }


    /**
     * Returns a {@link CheckedCast} that wraps the value transformed by
     * {@code mapper} in the eventuality that the cast was successful.
     *
     * @param mapper the mapper function
     * @param <U>    target type of the returned {@link CheckedCast} after the
     *               transformation
     * @return a {@link CheckedCast} that wraps the value transformed by
     * {@code mapper} in the eventuality that the cast was successful.
     */
    public <U> CheckedCast<U> map(Function<? super T, ? extends U> mapper) {
        Require.nonNull(mapper);
        if (value == null) {
            return empty();
        } else {
            try {
                return of(mapper.apply(value));
            } catch (ClassCastException ignored) {
                return empty();
            }
        }
    }
}
