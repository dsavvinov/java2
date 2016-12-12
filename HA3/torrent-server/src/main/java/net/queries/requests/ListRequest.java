package net.queries.requests;

import net.MessageHandler;
import net.Query;
import net.Message;
import net.queries.ListQuery;

import java.io.InputStream;
import java.io.OutputStream;

public class ListRequest implements Message {
    @Override
    public String toString() {
        return "";
    }

    public void readFrom(InputStream is) { }

    @Override
    public void writeTo(OutputStream out){ }

    @Override
    public Query getQuery() {
        return new ListQuery();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ListRequest;
    }

    @Override
    public <T> T dispatch(MessageHandler<T> handler) {
        return handler.handle(this);
    }
}
