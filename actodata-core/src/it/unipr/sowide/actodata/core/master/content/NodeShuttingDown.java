package it.unipr.sowide.actodata.core.master.content;

import it.unipr.sowide.actodata.core.master.Master;
import it.unipr.sowide.util.annotations.Singleton;

/**
 * Message used internally by ActoDatA actors to inform the {@link Master} that
 * the actor is shutting down.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
@Singleton
public enum NodeShuttingDown implements InformMaster{
    INSTANCE
}
