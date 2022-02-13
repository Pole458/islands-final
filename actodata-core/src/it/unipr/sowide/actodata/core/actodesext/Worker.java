package it.unipr.sowide.actodata.core.actodesext;

import it.unipr.sowide.actodata.core.actodesext.content.DoJob;
import it.unipr.sowide.actodes.actor.MessagePattern;
import it.unipr.sowide.actodes.actor.Shutdown;
import it.unipr.sowide.actodes.filtering.MessagePatternField;
import it.unipr.sowide.actodes.filtering.constraint.IsEqual;
import it.unipr.sowide.actodes.filtering.constraint.IsInstance;

import java.io.Serializable;

import static it.unipr.sowide.util.CheckedCast.cast;

/**
 * The behavior of a temporary worker that performs a job, returns the result
 * to its parent, and after that turns into {@link Shutdown#SHUTDOWN}.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public final class Worker extends ActoDataBaseBehavior {

    private MessagePattern doJobPattern(){
        return new MessagePattern(
                new MessagePatternField(
                        MessagePattern.MessageField.SENDER,
                        new IsEqual<>(getParent())
                ),
                new MessagePatternField(
                        MessagePattern.MessageField.CONTENT,
                        new IsInstance(DoJob.class)
                )
        );
    }


    @Override
    public void actoDataNodeCases(ActoDataCaseFactory c) {
        c.define(doJobPattern(), (message) -> {
            cast((DoJob<? extends Serializable>)null, message.getContent())
                    .ifSuccessful(doJob -> {
                        Serializable result = doJob.doJob();
                        send(message, result);
                    });
            return Shutdown.SHUTDOWN;
        });

    }
}
