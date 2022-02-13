package it.unipr.sowide.actodata.core.preprocessor;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * A {@link Preprocessor} that uses the {@link Stream} API to define operations
 * on instances of data.
 *
 * @param <I> the input data type
 * @param <O> the output data type
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class JavaStreamPreprocessor<I, O> extends Preprocessor<I, O> {

    private final Function<Stream<I>, Stream<O>> stream;

    /**
     * A preprocessor that uses operations on {@link Stream} to transform the
     * data.
     *
     * @param stream a function that takes in input a stream of {@link I}, and
     *               return a non-terminalized stream of {@link O}.
     */
    public JavaStreamPreprocessor(Function<Stream<I>, Stream<O>> stream) {
        this.stream = stream;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public void preprocess(I input, Consumer<O> resultAcceptor) {
        stream.apply(Stream.of(input)).forEach(resultAcceptor);
    }
}
