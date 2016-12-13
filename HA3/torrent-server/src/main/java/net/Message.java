package net;

import exceptions.InvalidProtocolException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Generic interface for messages which can be sent over the wire.
 *
 * Each message should have corresponding Query type.
 *
 * Message, along with MessageHandler, implements double-dispatching
 * idiom. Users can call `Message.dispatch()`, passing instance
 * of MessageHandler with desired overloads, and via 2 virtual calls
 * appropriate `handle()` for concrete type will be called.
 */
public interface Message {
    void readFrom(InputStream is) throws InvalidProtocolException;
    void writeTo(OutputStream os) throws IOException;
    Query getQuery();

    <T> T dispatch(MessageHandler<T> handler);
}
