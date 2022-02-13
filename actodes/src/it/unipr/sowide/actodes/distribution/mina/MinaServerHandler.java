package it.unipr.sowide.actodes.distribution.mina;

import java.util.HashSet;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.util.ConcurrentHashSet;

import it.unipr.sowide.actodes.distribution.mina.MinaConnector.ClientReference;
import it.unipr.sowide.actodes.registry.Reference;

/**
 *
 * The {@code MinaServerSessionHandler} class is an handler used by the broker
 * actor space for managing the discovery and the communication with the other
 * actor spaces.
 *
**/

public final class MinaServerHandler extends IoHandlerAdapter
{
  /**
   * Command for all discovered clients.
   *
  **/
  public static final String DISCOVER_REQUEST =
      "MinaServerSessionHandler.DISCOVER_REQUEST";

  /**
   * Command for address of a connected client.
   *
  **/
  public static final String CONNECTION_REQUEST =
      "MinaServerSessionHandler.CONNECTION_REQUEST";

  /**
   * Command to store client {@code Reference} in server client list.
   *
  **/
  public static final String REFERENCE_REQUEST =
      "MinaServerSessionHandler.REFERENCE_REQUEST";

  // Internal client list.
  private final ConcurrentHashSet<ClientReference> connectedClients;

  /**
   * Class constructor.
   *
  **/
  public MinaServerHandler()
  {
    this.connectedClients =  new ConcurrentHashSet<>();
  }

  /**
   * Manages asynchronous exceptions.
   *
   * @param s  the session where exception is raised.
   * @param c  the cause of exception.
   *
   * @throws Exception if an exception is thrown.
   *
  **/
  @Override
  public void exceptionCaught(final IoSession s, final Throwable c)
    throws Exception
  {
    throw new Exception(c);
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
    MinaServerRequest request = (MinaServerRequest) m;

    if (request.getQuery().contentEquals(REFERENCE_REQUEST)
        && (request.getData() != null)
        && (request.getData() instanceof ClientReference))
    {
      this.connectedClients.add((ClientReference) request.getData());
    }
    else if (request.getQuery().contentEquals(CONNECTION_REQUEST)
            && (request.getData() != null)
            && (request.getData() instanceof Reference))
    {
      ClientReference destinationClient = new ClientReference(null, null);

      for (ClientReference c: this.connectedClients)
      {
        String r = c.getReference().toString();

        if (r.contentEquals(request.getData().toString()))
        {
          destinationClient = c;
        }
      }

      s.write(destinationClient);
    }
    else if (request.getQuery().contentEquals(DISCOVER_REQUEST))
    {
      final HashSet<Reference> refs = new HashSet<Reference>();

      for (ClientReference client: this.connectedClients)
      {
        refs.add(client.getReference());
      }

      s.write(refs);
    }
  }
}
