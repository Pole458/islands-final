package it.unipr.sowide.actodata.core.actodesext;

import it.unipr.sowide.actodes.actor.MessagePattern;
import it.unipr.sowide.actodes.filtering.constraint.IsInstance;
import it.unipr.sowide.util.annotations.Namespace;

import java.util.HashMap;
import java.util.Map;

/**
 * Set of internal methods used by {@link ActoDataCaseFactory} implementation
 * to build {@link MessagePattern}s with {@link IsInstance} constraints.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 * @implNote all type - message pattern pairs are cached in static maps, so
 * only one pattern is created and reused for each type.
 */
@Namespace
public class IsInstancePatterns {
    private IsInstancePatterns() {
    } // don't instantiate

    private static final Map<Class<?>, IsInstance> alreadyCreatedConstraints
            = new HashMap<>();
    private static final Map<Class<?>, MessagePattern> alreadyCreatedPatterns
            = new HashMap<>();


    static IsInstance isInstance(Class<?> clazz) {
        return alreadyCreatedConstraints.computeIfAbsent(
                clazz,
                IsInstance::new
        );
    }

    static MessagePattern isInstanceContentPattern(Class<?> clazz) {
        return alreadyCreatedPatterns.computeIfAbsent(
                clazz,
                c -> MessagePattern.contentPattern(isInstance(c))
        );
    }
}
