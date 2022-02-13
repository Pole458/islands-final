package it.unipr.sowide.util.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * The types annotated with this annotation represent abstract data types which
 * are designed to be immutable.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
@Target(ElementType.TYPE)
public @interface Immutable {
}
