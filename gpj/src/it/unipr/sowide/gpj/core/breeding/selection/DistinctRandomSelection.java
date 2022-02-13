package it.unipr.sowide.gpj.core.breeding.selection;

import it.unipr.sowide.gpj.core.EvolutionContext;
import it.unipr.sowide.gpj.core.Individual;
import it.unipr.sowide.util.promise.Promise;
import it.unipr.sowide.util.promise.Promises;

import java.util.ArrayList;
import java.util.List;

public class DistinctRandomSelection<I extends Individual> extends Selection<I> {

    @Override
    public Promise<List<I>, Throwable> select(
            EvolutionContext evolutionContext,
            List<I> oldPopulation,
            int targetPopSize
    ) {
        List<I> tmp = new ArrayList<>(oldPopulation);
        List<I> result = new ArrayList<>();
        while (result.size() < targetPopSize) {
            int index = evolutionContext.getRandom().randomInt(
                    0,
                    tmp.size()
            );
            result.add(tmp.remove(index));
        }
        tmp.clear();
        return Promises.immediatelyResolve(result);
    }
}
