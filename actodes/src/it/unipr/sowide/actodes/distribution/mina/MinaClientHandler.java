package it.unipr.sowide.actodes.distribution.mina;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import it.unipr.sowide.actodes.actor.Message;
import it.unipr.sowide.actodes.distribution.Connector;

/**
 *
 * The {@code MinaClientHandler} class is an handler for the messages
 * received from the other actor spaces.
 *
**/

public final class MinaClientHandler extends IoHandlerAdapter
{
  // Connection manager.
  private final Connector connector;

  /**
   * Class constructor.
   *
   * @param c  the connection manager.
   *
  **/
  public MinaClientHandler(final Connector c)
  {
    this.connector = c;
  }

  /**
   * Manages a received message.
   *
   * @param s  the active session.
   * @param m  the message received.
   *
   * @throws Exception if received object is not a message.
   *
  **/
  @Override
  public void messageReceived(final IoSession s, final Object m)
      throws Exception
  {
    this.connector.manage((Message) m);
  }
}
