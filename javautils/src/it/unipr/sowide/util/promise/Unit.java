package it.unipr.sowide.util.promise;

import it.unipr.sowide.util.annotations.Singleton;

/**
 * Special value used by various promises as resolution value to identify
 * the succesful completion of a computation that do not returns any particular
 * kind of value (it is the equivalent of {@link Void} for the promise model
 * of computation).
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
@Singleton
public enum Unit {
    UNIT
}
