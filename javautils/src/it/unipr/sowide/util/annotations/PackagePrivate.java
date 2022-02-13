package it.unipr.sowide.util.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Just an annotation to make explicit that a declaration is meant to be
 * package-private.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
@Target({ElementType.ANNOTATION_TYPE,
        ElementType.FIELD,
        ElementType.METHOD,
        ElementType.TYPE,
        ElementType.CONSTRUCTOR
})
public @interface PackagePrivate {
}
