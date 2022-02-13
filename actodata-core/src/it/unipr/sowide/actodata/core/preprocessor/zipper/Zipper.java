package it.unipr.sowide.actodata.core.preprocessor.zipper;

import it.unipr.sowide.actodata.core.preprocessor.Preprocessor;
import it.unipr.sowide.util.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A {@link Zipper} is a {@link Preprocessor} that creates pairs of two kind
 * of input data and outputs {@link Pair} instances.
 *
 * @param <I>  the input type
 * @param <I1> the the subtype of I for the values of the first component of the
 *             pair
 * @param <I2> the the subtype of I for the values of the second component of
 *             the pair
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public abstract class Zipper<I, I1 extends I, I2 extends I>
        extends Preprocessor<I, Pair<I1, I2>> {
    private final Map<Object, I1> queued1 = new HashMap<>();
    private final Map<Object, I2> queued2 = new HashMap<>();

    private final Class<? extends I1> typeLeft;
    private final Class<? extends I2> typeRight;

    /**
     * A zipper that distinguishes the two components using the provided
     * classes.
     *
     * @param typeLeft  the type of the first component of the pair
     * @param typeRight the type of the second component of the pair
     */
    public Zipper(Class<? extends I1> typeLeft, Class<? extends I2> typeRight) {
        this.typeLeft = typeLeft;
        this.typeRight = typeRight;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public void preprocess(I input, Consumer<Pair<I1, I2>> resultAcceptor) {
        if (typeLeft.isInstance(input)) {
            @SuppressWarnings("unchecked")
            I1 i1 = (I1) input;
            Object token = associationTokenLeft(i1);
            if (queued2.containsKey(token)) {
                I2 i2 = queued2.remove(token);
                resultAcceptor.accept(Pair.pair(i1, i2));
            } else {
                queued1.put(token, i1);
            }
        } else if (typeRight.isInstance(input)) {
            @SuppressWarnings("unchecked")
            I2 i2 = (I2) input;
            Object token = associationTokenRight(i2);
            if (queued1.containsKey(token)) {
                I1 i1 = queued1.remove(token);
                resultAcceptor.accept(Pair.pair(i1, i2));
            } else {
                queued2.put(token, i2);
            }
        }
    }

    /**
     * Creates a token used to associate the value received with a future value
     *
     * @param input1 the input
     * @return the token
     */
    protected abstract Object associationTokenLeft(I1 input1);

    /**
     * Creates a token used to associate the value received with a future value
     *
     * @param input2 the input
     * @return the token
     */
    protected abstract Object associationTokenRight(I2 input2);
}
