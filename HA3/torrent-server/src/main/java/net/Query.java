package net;

/**
 * Generic type of possible Queries, that are sent over the wire,
 * e.g. "List", "Get", etc.
 */
public interface Query {
    /**
     * Return corresponding request-message of that Query, filled with
     * default values.
     */
    Message getEmptyRequest();

    /**
     * Return corresponding response-message of that Query, filled with
     * default values.
     */
    Message getEmptyResponse();

    /**
     * Return wire-id of that query. Used in Protocol implementations
     * to parse query type from the wire.
     */
    int getId();
}
