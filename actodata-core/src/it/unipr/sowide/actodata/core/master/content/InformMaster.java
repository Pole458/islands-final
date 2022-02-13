package it.unipr.sowide.actodata.core.master.content;

import it.unipr.sowide.actodata.core.master.MasterInterface;
import it.unipr.sowide.actodes.interaction.Inform;

/**
 * Supertype of messages that can be used by actors with
 * {@link MasterInterface#informMaster(InformMaster)} to inform the master of
 * a particular event in the application.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public interface InformMaster extends Inform {
}
