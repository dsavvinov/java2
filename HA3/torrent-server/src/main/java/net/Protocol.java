package net;

import exceptions.InvalidProtocolException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Generic interface of Protocol - stateless class, responsible
 * for serializing/de-serializing Messages in/from wire.
 *
 * Note that part of serialization logic is implemented in Messages themselves.
 */
public interface Protocol {
    Query parseQueryType(InputStream is) throws InvalidProtocolException;

    Message readRequest(InputStream is) throws InvalidProtocolException;
    void writeRequest(Message request, OutputStream os) throws IOException;

    Message readResponse(Query initialQuery, InputStream is) throws InvalidProtocolException;
    void writeResponse(Message response, OutputStream os) throws IOException;
}
