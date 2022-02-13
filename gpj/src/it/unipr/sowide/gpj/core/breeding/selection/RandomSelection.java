package it.unipr.sowide.gpj.core.breeding.selection;

import it.unipr.sowide.gpj.core.EvolutionContext;
import it.unipr.sowide.gpj.core.Individual;
import it.unipr.sowide.util.promise.Promise;

import java.util.ArrayList;
import java.util.List;

import static it.unipr.sowide.util.promise.Promises.immediatelyResolve;

public class RandomSelection<I extends Individual> extends Selection<I>{

    @Override
    public Promise<List<I>, Throwable> select(
            EvolutionContext evolutionContext,
            List<I> oldPopulation,
            int targetPopSize
    ) {
        if(oldPopulation.isEmpty()){
            return immediatelyResolve(oldPopulation);
        }

        List<I> result = new ArrayList<>();
        for (int i = 0; i < targetPopSize; i++) {
            result.add(evolutionContext.getRandom().pick(oldPopulation));
        }

        return immediatelyResolve(result);
    }
}
