package it.unipr.sowide.islands;

import it.unipr.sowide.gpj.core.EvolutionContext;
import it.unipr.sowide.util.promise.Promise;
import it.unipr.sowide.util.promise.Promises;

import java.util.Optional;

public abstract class IslandIndividual implements it.unipr.sowide.gpj.core.Individual {

    private int birthGeneration;
    private Double fitness;
    private boolean inHallOfFame;

    @Override
    public int getBirthGeneration() {
        return birthGeneration;
    }

    @Override
    public void setBirthGeneration(int gen) {
        birthGeneration = gen;
    }

    @Override
    public Optional<Double> getFitness() {
        return Optional.ofNullable(fitness);
    }

    @Override
    public void setFitness(double value) {
        fitness = value;
    }

    @Override
    public void resetFitness() {
        fitness = null;
    }

    @Override
    public void kill() { }

    @Override
    public boolean isInHallOfFame() {
        return inHallOfFame;
    }

    @Override
    public void setInHallOfFame(boolean inHallOfFame) {
        this.inHallOfFame = inHallOfFame;
    }

    @Override
    public Promise<it.unipr.sowide.gpj.core.Individual, Throwable> copyIndividual() {
        IslandIndividual islandIndividual = copyIndividualWithoutMetaData();
        copyMetaDataTo(islandIndividual);
        return Promises.immediatelyResolve(islandIndividual);
    }

    public abstract IslandIndividual copyIndividualWithoutMetaData();

    public void copyMetaDataTo(IslandIndividual islandIndividual) {
        islandIndividual.setBirthGeneration(getBirthGeneration());
        if(getFitness().isPresent()) islandIndividual.setFitness(getFitness().get());
        else islandIndividual.resetFitness();
    }

    public abstract IslandIndividual mutate(EvolutionContext evolutionContext);

    @Override
    public abstract String toString();
}
