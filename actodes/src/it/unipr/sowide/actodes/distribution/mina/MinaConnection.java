package it.unipr.sowide.actodes.distribution.mina;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import it.unipr.sowide.actodes.actor.Message;
import it.unipr.sowide.actodes.distribution.Connection;
import it.unipr.sowide.actodes.distribution.Connector;
import it.unipr.sowide.actodes.registry.Reference;

/**
 *
 * The {@code MinaConnection} class supports the communication towards a remote
 * actor space taking advantage of Apache MINA asynchronous network sockets.
 *
**/

public final class MinaConnection
       extends IoHandlerAdapter implements Connection
{
  // Destination service provider reference.
  private final Reference destination;
  // Connection.
  private final NioSocketConnector connection;
  // Session.
  private final IoSession session;
  // Connector.
  private final Connector connector;

  /**
   * Constructor. Initializer and open a new connection with the client.
   *
   * @param r
   *
   * the actor space service provider reference of the destination actor space.
   *
   * @param a  the IP address associated with the destination actor space.
   * @param c  the connection manager.
   *
   * @throws IOException if it cannot establish to the connection.
   *
  **/
  public MinaConnection(
      final Reference r, final InetSocketAddress a, final Connector c)
          throws IOException
  {
    final int size = 2048;

    this.destination = r;
    this.connector = c;

    this.connection = new NioSocketConnector();
    this.connection.getFilterChain().addLast(
        "codec",
        new ProtocolCodecFilter(new org.apache.mina.filter.codec.
            serialization.ObjectSerializationCodecFactory()));

    this.connection.setHandler(this);
    this.connection.getSessionConfig().setReadBufferSize(size);

    ConnectFuture connectFuture = this.connection.connect(a);

    if (!connectFuture.awaitUninterruptibly().isConnected())
    {
      throw new IOException();
    }

    this.session = connectFuture.getSession();
  }

  /** {@inheritDoc} **/
  @Override
  public Reference getDestination()
  {
    return this.destination;
  }

  /** {@inheritDoc} **/
  @Override
  public boolean forward(final Message m)
  {
    return this.session.write(m).awaitUninterruptibly().isWritten();
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

  /**
   * Close the connection with the destination actor space.
   *
  **/
  public void close()
  {
    this.session.closeNow();
    this.connection.dispose(false);
  }
}
