package it.unipr.sowide.gpj.core.logbook;

public class EvaluationsDoneEntry extends EvolutionLogbookEntry {
    public EvaluationsDoneEntry(int evaluations, int generationCount) {
        super(generationCount, "Fitness evaluations completed " +
                "(effective evaluations: "+evaluations+")");
    }
}
