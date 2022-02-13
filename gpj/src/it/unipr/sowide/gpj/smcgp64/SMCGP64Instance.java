package it.unipr.sowide.gpj.smcgp64;

/**
 * An instance in the form of a tuple of 4 32-bit values.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class SMCGP64Instance {
    private final int clas;
    private final long pat1;
    private final long pat2;

    public SMCGP64Instance(int clas, long pat1, long pat2) {
        this.clas = clas;
        this.pat1 = pat1;
        this.pat2 = pat2;
    }

    public int getClas() {
        return clas;
    }

    public long getPat1() {
        return pat1;
    }

    public long getPat2() {
        return pat2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SMCGP64Instance that = (SMCGP64Instance) o;

        if (getClas() != that.getClas()) return false;
        if (getPat1() != that.getPat1()) return false;
        return getPat2() == that.getPat2();
    }

    @Override
    public int hashCode() {
        int result = getClas();
        result = 31 * result + (int) (getPat1() ^ (getPat1() >>> 32));
        result = 31 * result + (int) (getPat2() ^ (getPat2() >>> 32));
        return result;
    }
}
