package it.unipr.sowide.util.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * The types annotated with this annotation <b>do not</b> represent any type,
 * but are simply used as declatation space for static methods and fields.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
@Target(ElementType.TYPE)
public @interface Namespace {
}
