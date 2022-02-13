package it.unipr.sowide.gpj.smcgp64;

/**
 * Class which contains the 64-bit values used as arguments/context for trees.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class SMC64PatternArguments {
    private long pat1;
    private long pat2;

    public SMC64PatternArguments() {
    }

    public long getPat1() {
        return pat1;
    }

    public long getPat2() {
        return pat2;
    }

    public void setPat1(long pat1) {
        this.pat1 = pat1;
    }

    public void setPat2(long pat2) {
        this.pat2 = pat2;
    }

}
