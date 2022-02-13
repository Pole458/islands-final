package it.unipr.sowide.actodata.core.acquirer.content;

import it.unipr.sowide.actodata.core.acquirer.Acquirer;
import it.unipr.sowide.actodes.interaction.Request;
import it.unipr.sowide.util.annotations.Singleton;

/**
 * Message that can be sent to an acquirer to request him to pause itself.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 * @see Acquirer#pause()
 */
@Singleton
public enum PauseAcquirer implements Request {
    INSTANCE
}
