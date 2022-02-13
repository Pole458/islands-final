package it.unipr.sowide.actodes.filtering;


/**
 *
 * The {@code RegexeMatcher} enumeration defines a pattern matcher
 * that filters objects applying a regular expression to their
 * string representation.
 *
 * This enumeration has a single element: <code>INSTANCE</code> an it is used
 * for proving a thread safe implementation of the singleton pattern.
 *
**/

public enum RegexMatcher implements Matcher<String, RegexPattern>
{
  /**
   * The singleton instance.
   *
  **/
  INSTANCE;

  /** {@inheritDoc} **/
  @Override
  public boolean match(final String o, final RegexPattern p)
  {
    return o.matches(p.getValue());
  }
}
