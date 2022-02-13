package it.unipr.sowide.gpj.smcgp32;

/**
 * An instance in the form of a tuple of 4 32-bit values.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class SMCGP32Instance {
    private final int clas;
    private final int pat1;
    private final int pat2;
    private final int pat3;
    private final int pat4;

    public SMCGP32Instance(int clas, int pat1, int pat2, int pat3, int pat4) {
        this.clas = clas;
        this.pat1 = pat1;
        this.pat2 = pat2;
        this.pat3 = pat3;
        this.pat4 = pat4;
    }


    public int getClas() {
        return clas;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SMCGP32Instance that = (SMCGP32Instance) o;

        if (getClas() != that.getClas()) return false;
        if (getPat1() != that.getPat1()) return false;
        if (getPat2() != that.getPat2()) return false;
        if (getPat3() != that.getPat3()) return false;
        return getPat4() == that.getPat4();
    }

}
