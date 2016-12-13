package net.queries.requests;

import exceptions.InvalidProtocolException;
import net.MessageHandler;
import net.Query;
import net.Message;
import net.queries.StatQuery;

import java.io.*;

public class StatRequest implements Message {
    private int id;

    public StatRequest() { }

    public StatRequest(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public void readFrom(InputStream is) throws InvalidProtocolException {
        try {
            id = new DataInputStream(is).readInt();
        } catch (IOException e) {
            throw new InvalidProtocolException("Format error during reading request: " + e.getMessage());
        }
    }

    @Override
    public void writeTo(OutputStream os) throws IOException {
        new DataOutputStream(os).writeInt(id);
    }

    @Override
    public Query getQuery() {
        return new StatQuery();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StatRequest that = (StatRequest) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public <T> T dispatch(MessageHandler<T> handler) {
        return handler.handle(this);
    }
}
