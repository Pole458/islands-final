package it.unipr.sowide.gpj.tree;

import it.unipr.sowide.util.RandomUtils;

/**
 * A constant whose value is randomly generated the first time it is evaluated
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class EphemeralRandomDouble<C> extends LateInitConstant<Double, C> {

    private final RandomUtils random;
    private final double lowerBound;
    private final double upperBound;

    public EphemeralRandomDouble(
            RandomUtils random,
            double lowerBound,
            double upperBound
    ) {
        super(() -> (upperBound - lowerBound)
                * random.nextDouble() + lowerBound);
        this.random = random;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }


    @Override
    public EphemeralRandomDouble<C> copy() {
        return new EphemeralRandomDouble<>(random, lowerBound, upperBound);
    }
}
