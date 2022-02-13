package it.unipr.sowide.actodata.core.actodesext.content;

import it.unipr.sowide.actodes.interaction.Request;

/**
 * Functional interface that represents a Request message to an other actor to
 * perform a particular job
 *
 * @param <T> the type of the result of the job
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
@FunctionalInterface
public interface DoJob<T> extends Request {
    T doJob();
}
