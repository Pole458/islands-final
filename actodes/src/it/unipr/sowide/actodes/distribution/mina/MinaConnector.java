package it.unipr.sowide.actodes.distribution.mina;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import it.unipr.sowide.actodes.actor.Behavior;
import it.unipr.sowide.actodes.distribution.Connection;
import it.unipr.sowide.actodes.distribution.Connector;
import it.unipr.sowide.actodes.error.ErrorManager;
import it.unipr.sowide.actodes.registry.Reference;

/**
 *
 * The {@code MinaConnector} class manages the communication towards
 * remote actor spaces taking advantage of the Apache MINA asynchronous
 * network sockets.
 *
**/
public final class MinaConnector extends Connector
{
  private static final long serialVersionUID = 1L;

  private static final String ADDR = "127.0.0.1";
  private static final int PORT    = 1100;

  // Master client server socket.
  private NioSocketAcceptor serverAcceptor = null;
  // Client server socket for incoming messages.
  private NioSocketAcceptor clientAcceptor = null;
  // Client connection with master.
  private NioSocketConnector connectiorWithServer = null;

  // ActiveBehavior connection with master.
  private ConnectFuture serverConnection = null;
  // Client reference and listen address.
  private ClientReference clientReference = null;

  /**
   * Class constructor.
   *
   * This connector uses the default configuration
   * and does not act as communication broker.
   *
  **/
  protected MinaConnector()
  {
    configure(ADDR, PORT);
  }

  /**
   * Class constructor.
   *
   * @param f
   *
   * a boolean flag indicating if the connector act as communication
   * broker (value = <code>true</code>) or not (value = <code>false</code>).
   *
   * This connector uses the default configuration: the values of the IP
   * address and port of the broker connector are <code>127.0.0.1</code>
   * and <code>1100</code>.
   *
   * Note that the IP address, 127.0.0.1, is used for indicating
   * the local host.
   *
  **/
  public MinaConnector(final boolean f)
  {
    setBroker(f);

    configure(ADDR, PORT);
  }

  /**
   * Class constructor.
   *
   * @param p  the IP port.
   *
   * This connector acts as communication broker.
   *
   */
  public MinaConnector(final int p)
  {
    setBroker(true);

    configure("localhost", p);
  }

  /**
   * Class constructor.
   *
   * @param a  the IP address.
   * @param p  the IP port.
   *
   * This connector does not act as communication broker.
   *
   */
  public MinaConnector(final String a, final int p)
  {
    configure(a, p);
  }


  /**
   * Configures the Apache Mina connector.
   *
   * @param a
   *
   * the IP address of the host where the broker connector is running.
   *
   * @param p
   *
   * the IP port used by the broker connector for receiving messages.
   *
  **/
  public void configure(final String a, final int p)
  {
    try
    {
      final int size = 2048;

      if (isBroker())
      {
        // Initializer a TCP server for master
        this.serverAcceptor = new NioSocketAcceptor();

        this.serverAcceptor.getFilterChain().addLast(
            "codec",
            new ProtocolCodecFilter(new org.apache.mina.filter.codec.
                serialization.ObjectSerializationCodecFactory()));

        this.serverAcceptor.setHandler(new MinaServerHandler());
        this.serverAcceptor.getSessionConfig().setReadBufferSize(size);
        this.serverAcceptor.bind(new InetSocketAddress(p));
      }

      // Initializer a TCP server for master
      this.clientAcceptor = new NioSocketAcceptor();

      this.clientAcceptor.getFilterChain().addLast(
          "codec",
          new ProtocolCodecFilter(new org.apache.mina.filter.codec.
              serialization.ObjectSerializationCodecFactory()));

      this.clientAcceptor.setHandler(new MinaClientHandler(this));
      this.clientAcceptor.getSessionConfig().setReadBufferSize(size);
      this.clientAcceptor.bind(new InetSocketAddress(0));

      // Connect to TCP server to register in available client
      this.connectiorWithServer = new NioSocketConnector();
      this.connectiorWithServer.getFilterChain().addLast(
          "codec",
          new ProtocolCodecFilter(new org.apache.mina.filter.codec.
              serialization.ObjectSerializationCodecFactory()));

      this.connectiorWithServer.getSessionConfig().setReadBufferSize(size);
      this.connectiorWithServer.getSessionConfig().setUseReadOperation(true);

      final InetSocketAddress serverConnAddress = new InetSocketAddress(a, p);

      this.serverConnection =
          this.connectiorWithServer.connect(serverConnAddress);

      if (!this.serverConnection.awaitUninterruptibly().isConnected())
      {
        throw new Exception();
      }
    }
    catch (Exception e)
    {
      ErrorManager.kill(e);
    }
  }

  /** {@inheritDoc} **/
  @Override
  public void start()
  {
    try
    {
      this.clientReference = new ClientReference(
          this.clientAcceptor.getLocalAddress(), Behavior.PROVIDER);

      sendReferenceToServer();
    }
    catch (Exception e)
    {
      ErrorManager.kill(e);
    }

    super.start();
  }

  /**
   *
   * The {@code ClientReference} class mantains the information about
   * IP address and port of the actor spaces of the application.
   *
   * @author
   * Alessandro Bacchini, Agostino Poggi - AOT Lab - DII - University of Parma
   *
  **/
  public static class ClientReference implements Serializable
  {
    private static final long serialVersionUID = 1L;

    // Listening address and port.
    private final InetSocketAddress tcpAcceptorAddress;
    // Actor space reference.
    private final Reference reference;

    /**
     * Class constructor.
     *
     * @param a the IP address associated with the actor space.
     * @param r the actor space reference.
     *
    **/
    public ClientReference(final InetSocketAddress a, final Reference r)
    {
      this.tcpAcceptorAddress = a;
      this.reference          = r;
    }

    /** {@inheritDoc} **/
    @Override
    public String toString()
    {
      if ((this.tcpAcceptorAddress != null)
          && (this.reference != null))
      {
        return this.tcpAcceptorAddress.toString()
            + "[" +  this.reference.toString() + "]";
      }
      else
      {
        return "NullReference";
      }
    }

    /**
     * Gets the the actor space reference.
     *
     * @return the reference.
     *
    **/
    public Reference getReference()
    {
      return this.reference;
    }
  }

  /**
   * Sends a client reference.
   *
   * @throws IOException if operation fails.
  **/
  private void sendReferenceToServer() throws IOException
  {
    MinaServerRequest m = new MinaServerRequest(
        MinaServerHandler.REFERENCE_REQUEST, this.clientReference);

        if (!this.serverConnection.getSession().write(
               m).awaitUninterruptibly().isWritten())
    {
      throw new IOException();
    }
  }

  /** {@inheritDoc} **/
  @SuppressWarnings("unchecked")
  @Override
  protected Set<Reference> discover()
  {
    HashSet<Reference> references = new HashSet<Reference>();

    IoSession session = this.serverConnection.getSession();

    WriteFuture sessionWrite = session.write(new MinaServerRequest(
        MinaServerHandler.DISCOVER_REQUEST, null));

    if (!sessionWrite.awaitUninterruptibly().isWritten())
    {
      return references;
    }

    ReadFuture sessionRead = session.read();

    if (!sessionRead.awaitUninterruptibly().isRead())
    {
      return references;
    }

    Object obj = sessionRead.getMessage();

    if (obj instanceof HashSet<?>)
    {
      references = (HashSet<Reference>) obj;
    }

    return references;
  }

  /** {@inheritDoc} **/
  @Override
  protected Connection create(final Reference d)
  {
    try
    {
      final IoSession creatingConnection = this.serverConnection.getSession();

      final WriteFuture interfaceSendWrite = creatingConnection.write(
          new MinaServerRequest(MinaServerHandler.CONNECTION_REQUEST, d));

      if (!interfaceSendWrite.awaitUninterruptibly().isWritten())
      {
        throw new IOException();
      }

      final ReadFuture referenceAddressRquest = creatingConnection.read();

      if (!referenceAddressRquest.awaitUninterruptibly().isRead())
      {
        throw new IOException();
      }

      final ClientReference clientRef =
              (ClientReference) referenceAddressRquest.getMessage();

      final MinaConnection connection = new MinaConnection(
          clientRef.reference, clientRef.tcpAcceptorAddress, this);

      return connection;
    }
    catch (Exception e)
    {
      ErrorManager.notify(e);
    }

    return null;
  }

  /** {@inheritDoc} **/
  @Override
  protected void destroy(final Reference d)
  {
    MinaConnection connection = (MinaConnection) get(d);

    if (connection == null)
    {
      return;
    }

    connection.close();
  }

  /** {@inheritDoc} **/
  @Override
  protected void destroy()
  {
    if (this.serverAcceptor != null)
    {
      this.serverAcceptor.unbind();
      this.serverAcceptor.dispose(false);
    }

    this.clientAcceptor.unbind();
    this.clientAcceptor.dispose(false);

    this.serverConnection.getSession().closeNow();
    this.connectiorWithServer.dispose(false);
  }
}
