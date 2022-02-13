package it.unipr.sowide.gpj.smcgp32;

/**
 * Class which contains the 32-bit values used as arguments/context for trees.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class SMC32PatternArguments {
    private int pat1;
    private int pat2;
    private int pat3;
    private int pat4;

    public SMC32PatternArguments() {
    }

    public int getPat1() {
        return pat1;
    }

    public int getPat2() {
        return pat2;
    }

    public int getPat3() {
        return pat3;
    }

    public int getPat4() {
        return pat4;
    }

    public void setPat1(int pat1) {
        this.pat1 = pat1;
    }

    public void setPat2(int pat2) {
        this.pat2 = pat2;
    }

    public void setPat3(int pat3) {
        this.pat3 = pat3;
    }

    public void setPat4(int pat4) {
        this.pat4 = pat4;
    }
}
