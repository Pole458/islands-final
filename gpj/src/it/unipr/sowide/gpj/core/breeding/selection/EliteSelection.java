package it.unipr.sowide.gpj.core.breeding.selection;

import it.unipr.sowide.gpj.core.EvolutionContext;
import it.unipr.sowide.gpj.core.Individual;
import it.unipr.sowide.util.promise.Promise;
import it.unipr.sowide.util.promise.Promises;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class EliteSelection<I extends Individual> extends Selection<I> {
    @Override
    public Promise<List<I>, Throwable> select(
            EvolutionContext evolutionContext,
            List<I> oldPopulation,
            int targetPopSize
    ) {
        SortedSet<I> best = new TreeSet<>();

        for (I desc : oldPopulation) {
            best.add(desc);
            if (best.size() > targetPopSize) {
                best.remove(best.last());
            }
        }

        return Promises.immediatelyResolve(new ArrayList<>(best));
    }
}
