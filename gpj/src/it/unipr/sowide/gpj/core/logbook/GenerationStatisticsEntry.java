package it.unipr.sowide.gpj.core.logbook;

import java.util.Map;

public class GenerationStatisticsEntry extends EvolutionLogbookEntry {
    public GenerationStatisticsEntry(int generationCount, Map<String, Double> statistics) {
        super(generationCount, "Generation done. Statistics: \n  " +
                statistics.entrySet().stream()
                        .map(e -> ""+e.getKey()+": "+e.getValue())
                        .reduce("", (s1, s2) -> s1+"\n  "+s2)+"\n\n");
    }
}
