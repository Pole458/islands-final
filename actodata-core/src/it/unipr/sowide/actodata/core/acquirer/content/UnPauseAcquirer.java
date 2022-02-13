package it.unipr.sowide.actodata.core.acquirer.content;

import it.unipr.sowide.actodata.core.acquirer.Acquirer;
import it.unipr.sowide.actodes.interaction.Request;

/**
 * Message that can be sent to an acquirer to request him to unpause itself.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 * @see Acquirer#unpause()
 */
public enum  UnPauseAcquirer implements Request {
    INSTANCE
}
