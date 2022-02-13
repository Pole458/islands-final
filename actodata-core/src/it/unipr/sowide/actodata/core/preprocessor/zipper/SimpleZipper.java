package it.unipr.sowide.actodata.core.preprocessor.zipper;

/**
 * Simple {@link Zipper} that associates input values by the order in which
 * they are received by the actor.
 *
 * @param <I>  the input type
 * @param <I1> the the subtype of I for the values of the first component of the
 *             pair
 * @param <I2> the the subtype of I for the values of the second component of
 *             the pair
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class SimpleZipper<I, I1 extends I, I2 extends I>
        extends Zipper<I, I1, I2> {

    private int counter1 = 0;
    private int counter2 = 0;

    /**
     * A zipper that distinguishes the two components using the provided
     * classes.
     *
     * @param typeLeft  the type of the first component of the pair
     * @param typeRight the type of the second component of the pair
     */
    public SimpleZipper(
            Class<? extends I1> typeLeft,
            Class<? extends I2> typeRight
    ) {
        super(typeLeft, typeRight);
    }

    /** {@inheritDoc} **/
    @Override
    protected Object associationTokenLeft(I1 input1) {
        return counter1++;
    }

    /** {@inheritDoc} **/
    @Override
    protected Object associationTokenRight(I2 input2) {
        return counter2++;
    }
}
