package net;

import exceptions.InvalidProtocolException;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Abstract instance of Protocol, that implements as
 * much methods as possible.
 *
 * Successors of AbstractProtocol should implement
 * `parseQueryType()` method, that returns instance of Query,
 * corresponding to the content of InputStream.
 *
 * `parseQueryType()` should leave InputStream in such state,
 * so that `query.getEmptyRequest().readFrom()` reads proper
 * request
 */
public abstract class AbstractProtocol implements Protocol {
    public abstract Query parseQueryType(InputStream is) throws InvalidProtocolException;

    public Message readRequest(InputStream is) throws InvalidProtocolException {
        Query query = parseQueryType(is);
        Message request = query.getEmptyRequest();
        request.readFrom(is);
        return request;
    }

    public void writeRequest(Message request, OutputStream os) throws IOException {
        DataOutputStream out = new DataOutputStream(os);
        out.writeInt(request.getQuery().getId());
        request.writeTo(os);
    }

    public Message readResponse(Query initialQuery, InputStream is) throws InvalidProtocolException {
        Message response = initialQuery.getEmptyResponse();
        response.readFrom(is);
        return response;
    }

    public void writeResponse(Message response, OutputStream os) throws IOException {
        response.writeTo(os);
    }
}
