package it.unipr.sowide.gpj.smcgp64;

import it.unipr.sowide.gpj.tree.Tree;

/**
 * Contains the results of an evaluation angainst a test set of a SMCGP
 * classifier.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class SMCGP64SimpleEvaluation {
    private final Tree<Long, SMC64PatternArguments> classifier;
    private final double FPR;
    private final double FNR;
    private final double TPR;
    private final double TNR;
    private final int bestBit;
    private final double bestFitness;

    public SMCGP64SimpleEvaluation(
            Tree<Long, SMC64PatternArguments> classifier,
            double FPR,
            double FNR,
            double TPR,
            double TNR,
            int bestBit,
            double bestFitness
    ) {
        this.classifier = classifier;
        this.FPR = FPR;
        this.FNR = FNR;
        this.TPR = TPR;
        this.TNR = TNR;
        this.bestBit = bestBit;
        this.bestFitness = bestFitness;
    }

    public Tree<Long, SMC64PatternArguments> getClassifier() {
        return classifier;
    }

    /**
     * @return false positives rate
     */
    public double getFPR() {
        return FPR;
    }

    /**
     * @return false negatives rate
     */
    public double getFNR() {
        return FNR;
    }

    /**
     * @return true positives rate
     */
    public double getTPR() {
        return TPR;
    }

    /**
     * @return true negatives rate
     */
    public double getTNR() {
        return TNR;
    }

    /**
     * @return the found best bit of the classifier
     */
    public int getBestBit() {
        return bestBit;
    }

    /**
     * @return the fitness of the best bit of the classifier
     */
    public double getBestFitness() {
        return bestFitness;
    }

    @Override
    public String toString() {
        return "SMCGPSimpleEvaluation{" +
                ("\n\tFPR = " + FPR) +
                ("\n\tFNR = " + FNR) +
                ("\n\tTPR = " + TPR) +
                ("\n\tTNR = " + TNR) +
                ("\n\tBIT = " + bestBit) +
                ("\n\tFIT = " + bestFitness) +
                ("\n\tSIZ = " + classifier.size()) +
                ("\n\tHEI = " + classifier.height()) +
                "\n}";
    }
}
