package it.unipr.sowide.actodata.core.preprocessor;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A {@link Preprocessor} that uses a blocking function to implement a
 * transformation of each single instance of input data into a single instance
 * of output data.
 *
 * @param <I> the input data type
 * @param <O> the ouptut data type
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class Mapper<I, O> extends Preprocessor<I, O> {

    private final Function<I, O> function;

    /**
     * A mapper that uses the provided function to define the transfrmation
     * of data instances
     *
     * @param function the function
     */
    public Mapper(Function<I, O> function) {
        this.function = function;
    }

    /** {@inheritDoc} **/
    @Override
    public void preprocess(I input, Consumer<O> resultAcceptor) {
        resultAcceptor.accept(function.apply(input));
    }
}
