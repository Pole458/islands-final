package it.unipr.sowide.actodata.core.dataflow;

import it.unipr.sowide.actodes.registry.Reference;

/**
 * Interface representing the generic concept of an
 * "actor-reference-encapsulating object".
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public interface ContainsReference {
    /**
     * @return the reference
     */
    Reference getReference();
}
