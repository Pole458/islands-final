package it.unipr.sowide.actodata.core.engine;


import it.unipr.sowide.actodes.interaction.Inform;
import it.unipr.sowide.util.annotations.Singleton;

/**
 * Message signaling that the actor that sent the message is an engine and it
 * is ready to carry tasks.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
@Singleton
public enum  EngineReady implements Inform {
    INSTANCE
}
