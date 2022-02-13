package it.unipr.sowide.util.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Declarations annotated by this annotation are designed to be
 * extendend/overridden, but only if needed.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
public @interface Open {
}
