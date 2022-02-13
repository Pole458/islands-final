package it.unipr.sowide.actodata.core.dataflow;


import it.unipr.sowide.actodata.core.dataflow.content.SubscribeForDataPush;
import it.unipr.sowide.actodata.core.dataflow.content.UnsubscribeForDataPush;
import it.unipr.sowide.actodes.actor.Behavior;

/**
 * A {@link it.unipr.sowide.actodata.core.actodesext.BehaviorFragment} that
 * keeps the references of a set of subscribers and provides the method to
 * send data to all the subscribers.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class PushSender extends SubscriberSetFragment {

    /**
     * A {@link PushSender} fragment
     */
    public PushSender() {
        super(SubscribeForDataPush.class, UnsubscribeForDataPush.class);
    }

    /**
     * Sends the specified data to all the subscribers.
     *
     * @param self the behavior using this fragment
     * @param data the data to be sent
     */
    public void sendData(Behavior self, Object data){
        sendToAll(self, data);
    }
}
