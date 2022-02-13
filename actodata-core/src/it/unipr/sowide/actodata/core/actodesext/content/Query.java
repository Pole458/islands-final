package it.unipr.sowide.actodata.core.actodesext.content;

import it.unipr.sowide.util.Require;
import it.unipr.sowide.actodes.actor.MessagePattern;
import it.unipr.sowide.actodes.filtering.constraint.And;
import it.unipr.sowide.actodes.filtering.constraint.IsInstance;
import it.unipr.sowide.actodes.filtering.constraint.UnaryConstraint;
import it.unipr.sowide.actodes.interaction.Request;

/**
 * Special type of message that represents the request to another actor to
 * perform a generic query.
 *
 * @param <QueryBodyType> the type of the body of the query
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
public class Query<QueryBodyType> implements Request {

    /**
     * Creates a {@link MessagePattern} for the reception of a query message
     * with the specified body type.
     *
     * @param queryBodyType the type of the body of the query
     * @param <T>           the type of the body of the query
     * @return the message pattern
     */
    public static <T> MessagePattern createPattern(
            Class<? extends T> queryBodyType
    ) {
        return createPattern(queryBodyType, null);
    }

    /**
     * Creates a {@link MessagePattern} for the reception of a query message
     * with the specified body type and additional constraint
     *
     * @param queryBodyType   the type of the body of the query
     * @param otherConstraint the additional constraint
     * @param <T>             the type of the body of the query
     * @return the message pattern
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T> MessagePattern createPattern(
            Class<? extends T> queryBodyType,
            UnaryConstraint<T> otherConstraint
    ) {
        Require.nonNull(queryBodyType);

        if (otherConstraint == null) {
            return MessagePattern.contentPattern(
                    new And<>(
                            new IsInstance(Query.class),
                            o -> queryBodyType.isInstance(((Query) o).body)
                    )
            );
        } else {
            return MessagePattern.contentPattern(new And(
                    new IsInstance(Query.class),
                    o -> {
                        if (o instanceof Query) {
                            Object body = ((Query) o).body;
                            return queryBodyType.isInstance(body)
                                    && otherConstraint.eval((T) body);
                        } else {
                            return false;
                        }
                    }
            ));
        }
    }

    private final QueryBodyType body;

    /**
     * Creates a query message with the specified body
     *
     * @param body the body
     */
    public Query(QueryBodyType body) {
        Require.nonNull(body);
        this.body = body;
    }

    /**
     * @return the body of the query
     */
    public QueryBodyType getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "Query{" +
                "body=" + body +
                '}';
    }
}
