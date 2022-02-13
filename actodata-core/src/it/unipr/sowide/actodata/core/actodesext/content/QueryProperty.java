package it.unipr.sowide.actodata.core.actodesext.content;

import it.unipr.sowide.actodata.core.actodesext.ActoDataCaseFactory;
import it.unipr.sowide.actodata.core.actodesext.ActoDataSendingActions;
import it.unipr.sowide.actodes.interaction.Request;
import it.unipr.sowide.util.Require;
import it.unipr.sowide.actodes.filtering.constraint.UnaryConstraint;

/**
 * Special {@link Request} message used to retrieve "properties" from other
 * actors.
 *
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 * @see ActoDataSendingActions#queryProperty
 * @see ActoDataCaseFactory#serveProperty
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class QueryProperty implements Request {
    public static UnaryConstraint<QueryProperty> withName(String propertyName) {
        Require.notBlankString(propertyName);
        return (gp) -> gp.propertyName.equals(propertyName);
    }

    private final String propertyName;

    /**
     * Creates the message to request the value of the exposed property with the
     * specifified name.
     *
     * @param propertyName the name of the property
     */
    public QueryProperty(String propertyName) {
        Require.notBlankString(propertyName);
        this.propertyName = propertyName;
    }

    /**
     * @return the name of the property
     */
    public String getPropertyName() {
        return propertyName;
    }

    @Override
    public String toString() {
        return "QueryProperty{" +
                "propertyName='" + propertyName + '\'' +
                '}';
    }
}
