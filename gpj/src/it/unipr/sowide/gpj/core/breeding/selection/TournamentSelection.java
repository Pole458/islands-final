package it.unipr.sowide.gpj.core.breeding.selection;

import it.unipr.sowide.gpj.core.EvolutionContext;
import it.unipr.sowide.gpj.core.Individual;
import it.unipr.sowide.util.promise.Promise;
import it.unipr.sowide.util.promise.Promises;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class TournamentSelection<I extends Individual> extends Selection<I> {

    private final int tournamentSize;

    public TournamentSelection(int tournamentSize) {
        this.tournamentSize = tournamentSize;
    }

    @Override
    public Promise<List<I>, Throwable> select(
            EvolutionContext evolutionContext,
            List<I> oldPopulation,
            int targetPopSize
    ) {
        List<I> result = new ArrayList<>();
        for (int i = 0; i < targetPopSize; i++) {

            SortedSet<I> picked = new TreeSet<>();
            while (picked.size() < tournamentSize) {
                picked.add(evolutionContext.getRandom().pick(oldPopulation));
            }


            result.add(picked.first());

        }
        return Promises.immediatelyResolve(result);
    }
}
