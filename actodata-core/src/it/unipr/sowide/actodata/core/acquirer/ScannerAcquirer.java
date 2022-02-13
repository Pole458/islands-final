package it.unipr.sowide.actodata.core.acquirer;

import it.unipr.sowide.actodes.executor.passive.CycleScheduler;
import it.unipr.sowide.util.Require;
import it.unipr.sowide.util.annotations.Open;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * An acquirer designed to be used with a {@link CycleScheduler} that scans a
 * input stream with a {@link Scanner} and submits all the strings between
 * occurrences of the provided delimiter.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class ScannerAcquirer extends LoopingAcquirer<String> {
    private final InputStream inputStream;
    private final String delimiter;
    private final Charset charset;
    private Scanner scanner = null;

    /**
     * A ScannerAcqurier with the specified {@link InputStream} and the line
     * break as delimiter. So this acquirer returns all the lines in the input
     * stream decoded with the default charset.
     *
     * @param inputStream the input stream
     */
    public ScannerAcquirer(InputStream inputStream) {
        Require.nonNull(inputStream);
        this.inputStream = inputStream;
        this.delimiter = null;
        this.charset = null;
    }

    /**
     * A ScannerAcquirer with provided input stream and delimiter. The stream
     * is decoded with default charset.
     *
     * @param inputStream the input stream
     * @param delimiter   the delimiter
     */
    public ScannerAcquirer(InputStream inputStream, String delimiter) {
        Require.nonNull(inputStream);
        this.inputStream = inputStream;
        this.delimiter = delimiter;
        this.charset = null;
    }

    /**
     * A ScannerAcquirer with specified input stream, delimiter and charset.
     *
     * @param inputStream the input stream
     * @param delimiter   the delimiter
     * @param charset     the charset
     */
    public ScannerAcquirer(
            InputStream inputStream,
            String delimiter,
            Charset charset
    ) {
        Require.nonNull(inputStream);
        this.inputStream = inputStream;
        this.delimiter = delimiter;
        this.charset = charset;
    }

    /**
     * A ScannerAcquirer with specified input stream, line break as delimiter
     * and specified charset.
     *
     * @param inputStream the input stream
     * @param charset the charset
     */
    public ScannerAcquirer(InputStream inputStream, Charset charset) {
        Require.nonNull(inputStream);
        this.inputStream = inputStream;
        this.delimiter = null;
        this.charset = charset;
    }

    /** {@inheritDoc} **/
    @Override
    public void setup() {
        if (charset != null) {
            scanner = new Scanner(inputStream, charset);
        } else {
            scanner = new Scanner(inputStream);
        }
        if (usesDelimiter()) {
            assert delimiter != null;
            scanner.useDelimiter(delimiter);
        }
    }

    /** {@inheritDoc} **/
    @Override
    public void next(AcquiredDataAcceptor<String> acquiredDataAcceptor) {
        if (scanner != null && !isPaused()) {
            if (usesDelimiter()) {
                acquiredDataAcceptor.submit(scanner.next());
            } else {
                acquiredDataAcceptor.submit(scanner.nextLine());
            }
        }
    }

    /** {@inheritDoc} **/
    @Override
    public boolean hasNext() {

        if (scanner != null) {
            boolean usesDelimiter = usesDelimiter();
            try {
                if (usesDelimiter) {
                    return scanner.hasNext();
                } else {
                    return scanner.hasNextLine();
                }
            } catch (IllegalStateException e) {
                //gotten here because scanner is closed
                return false;
            }
        } else {
            return true;
        }
    }

    /** {@inheritDoc} **/
    @Override
    @Open
    public void onDoneAcquiring() {
        super.onDoneAcquiring();
        if (scanner != null) {
            scanner.close();
        }
    }

    /**
     * @return True if a custom delimiter has been specified for this
     * {@link ScannerAcquirer}. False if it uses the line break as delimiter.
     */
    public boolean usesDelimiter() {
        return delimiter != null;
    }
}
