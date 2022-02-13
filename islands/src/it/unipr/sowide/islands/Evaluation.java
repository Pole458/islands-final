package it.unipr.sowide.islands;

/**
 * Contains the results of an evaluation angainst a particular environment for an individual
 *
 */
public class Evaluation {
    public final IslandIndividual individual;
    public final double evaluationFitness;
    public final int fitnessFunctionsCalls;

    public Evaluation(IslandIndividual individual, double trainingFitness, int fitnessFunctionsCalls) {
        this.individual = individual;
        this.evaluationFitness = trainingFitness;
        this.fitnessFunctionsCalls = fitnessFunctionsCalls;
    }

    @Override
    public String toString() {
        return
                "Evaluated Fitness: = " + evaluationFitness
                + ", Trained fitness: " + (individual.getFitness().isPresent() ? individual.getFitness().get() : 0.0)
                + ", Individual:" + individual;
    }
}
