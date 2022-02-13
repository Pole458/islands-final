package it.unipr.sowide.actodata.core.preprocessor.reduction;

import it.unipr.sowide.actodata.core.preprocessor.Preprocessor;

import java.util.function.Consumer;

/**
 * A {@link Preprocessor} that reduces some of sets of the data that receives in
 * in input to a value that is sent to the output pipelines.
 *
 * @param <T> the type of the input data
 * @param <A> the type of the internal accumulator
 * @param <R> the type of the data sent in output after the finalization of the
 *            accumulator
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public abstract class Reducer<T, A, R> extends Preprocessor<T, R> {

    private A current = null;

    /**
     * {@inheritDoc}
     **/
    @Override
    public void preprocess(T input, Consumer<R> resultAcceptor) {
        if (current == null) {
            current = createAccumulator();
        }
        if (shouldCreateNewCollection(current)) {
            resultAcceptor.accept(finish(current));
            current = createAccumulator();
        }
        current = fold(current, input);
    }

    /**
     * Given the current accumulator, determines if the reducer should finalize
     * the accumulated value and send the output to the listening actors.
     */
    public abstract boolean shouldCreateNewCollection(A currentAccumulator);

    /**
     * @return an empty accumulator instance
     */
    public abstract A createAccumulator();

    /**
     * Folds the received value into the accumulator and returns the resulting
     * accumulator.
     *
     * @param accumulator the current accumulator
     * @param element     the received value
     * @return the resulting accumulator
     */
    public abstract A fold(A accumulator, T element);

    /**
     * Transforms the accumulator in a finalized value, ready to be sent to the
     * output pipelines.
     *
     * @param accumulatedData the current accumulator
     * @return the output value
     */
    public abstract R finish(A accumulatedData);
}
