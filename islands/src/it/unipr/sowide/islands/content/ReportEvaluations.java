package it.unipr.sowide.islands.content;

import it.unipr.sowide.actodes.interaction.Inform;
import it.unipr.sowide.actodes.registry.Reference;
import it.unipr.sowide.islands.Evaluation;
import it.unipr.sowide.islands.EvaluationsReporter;

import java.util.Map;

/**
 * Message used to send a set of information from the controller to the
 * {@link EvaluationsReporter} to let it update the history of evaluatios.
 *
 */
public class ReportEvaluations implements Inform {

    private final Map<Reference, Evaluation> evaluations;
    private final Map<Reference, String> groups;

    public ReportEvaluations(Map<Reference, Evaluation> evaluations, Map<Reference, String> groups) {
        this.evaluations = evaluations;
        this.groups = groups;
    }

    public Map<Reference, Evaluation> getEvaluations() {
        return evaluations;
    }

    public Map<Reference, String> getGroups() {
        return groups;
    }
}
