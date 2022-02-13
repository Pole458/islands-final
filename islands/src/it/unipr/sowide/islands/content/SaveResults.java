package it.unipr.sowide.islands.content;


import it.unipr.sowide.actodes.interaction.Request;
import it.unipr.sowide.islands.EvaluationsReporter;

import java.time.Instant;

/**
 * Message sent by the controller to the {@link EvaluationsReporter} to request
 * to store the results of the simulation.
 *
 */
public class SaveResults implements Request {
    private final Instant startTime;
    private final Instant endTime;
    private final String filePath;
    private final String forOnlyGroup;

    /**
     * Requests to save the results to the specified filepath.
     *
     * @param startTime the instant at which the simulation started
     * @param endTime   the instant at which the simulation ended
     * @param filePath  the path of the file to which results are saved.
     */
    public SaveResults(Instant startTime, Instant endTime, String filePath) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.filePath = filePath;
        this.forOnlyGroup = null;
    }

    /**
     * Requests to save the results to the specified filepath.
     *
     * @param startTime    the instant at which the simulation started
     * @param endTime      the instant at which the simulation ended
     * @param filePath     the path of the file to which results are saved
     * @param forOnlyGroup specifies that the results to be outputted have to be
     *                     only the ones regarding the islands belonging to the
     *                     group
     */
    public SaveResults(
            Instant startTime,
            Instant endTime,
            String filePath,
            String forOnlyGroup
    ) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.filePath = filePath;
        this.forOnlyGroup = forOnlyGroup;
    }

    public String getFilePath() {
        return filePath;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public String getForOnlyGroup() {
        return forOnlyGroup;
    }
}
