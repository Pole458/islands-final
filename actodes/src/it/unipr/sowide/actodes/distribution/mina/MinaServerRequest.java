package it.unipr.sowide.actodes.distribution.mina;

import java.io.Serializable;

/**
 *
 * The {@code MinaServerRequest} class encapsulates the requests to the broker
 * actor space.
 *
**/

public final class MinaServerRequest implements Serializable
{
  private static final long serialVersionUID = 1L;

  // Command query.
  private final String request;
  // Command data.
  private final Serializable data;

  /**
   * Class constructor.
   *
   * @param r the request query.
   * @param d the request data.
   */
  public MinaServerRequest(final String r, final Serializable d)
  {
    this.request = r;
    this.data    = d;
  }

  /**
   * Gets the query of the request.
   *
   * @return the query.
   *
  **/
  public String getQuery()
  {
    return this.request;
  }

  /**
   * Gets the data of the request.
   *
   * @return the data.
   *
  **/
  public Serializable getData()
  {
    return this.data;
  }
}
