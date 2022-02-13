package it.unipr.sowide.actodata.core.acquirer;

import it.unipr.sowide.actodes.executor.passive.CycleScheduler;
import it.unipr.sowide.util.Require;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;

/**
 * An acquirer that can be used with a {@link CycleScheduler}. This kind of
 * acquirer submits to the subscribing actors the String data inside a file.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class SingleFileContentAcquirer extends ScannerAcquirer {


    /**
     * A new acquirer that submits the lines of the provide file, using the
     * default charset
     *
     * @param file the file
     * @throws FileNotFoundException if the file is not found
     */
    public SingleFileContentAcquirer(File file) throws FileNotFoundException {
        super(new FileInputStream(file));
        Require.readable(file);
    }

    /**
     * A new acquirer that submits the lines of the provided file, using the
     * provided charset
     *
     * @param file    the file
     * @param charset the charset
     * @throws FileNotFoundException if the file is not found
     */
    public SingleFileContentAcquirer(
            File file,
            Charset charset
    ) throws FileNotFoundException {
        super(new FileInputStream(file), charset);
        Require.readable(file);
    }

    /**
     * A new acquirer that submits the strings read from the provided file,
     * using the specified delimiter, and the default charset.
     *
     * @param file      the file
     * @param delimiter the delimiter
     * @throws FileNotFoundException if the file is not found
     */
    public SingleFileContentAcquirer(
            File file,
            String delimiter
    ) throws FileNotFoundException {
        super(new FileInputStream(file), delimiter);
        Require.readable(file);
    }

    /**
     * A new acquirer that submits the strings read from the provided file,
     * using the specified delimiter, and the specified charset.
     *
     * @param file      the file
     * @param delimiter the delimiter
     * @param charset   the charset
     * @throws FileNotFoundException if the file is not found
     */
    public SingleFileContentAcquirer(
            File file,
            String delimiter,
            Charset charset
    ) throws FileNotFoundException {
        super(new FileInputStream(file), delimiter, charset);
        Require.readable(file);
    }


}
