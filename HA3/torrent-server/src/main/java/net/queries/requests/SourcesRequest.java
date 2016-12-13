package net.queries.requests;

import exceptions.InvalidProtocolException;
import net.MessageHandler;
import net.Query;
import net.Message;
import net.queries.SourcesQuery;

import java.io.*;

public class SourcesRequest implements Message {
    private int requestedId;

    public int getRequestedId() {
        return requestedId;
    }

    public SourcesRequest() {}

    public SourcesRequest(int requestedId) {
        this.requestedId = requestedId;
    }

    @Override
    public String toString() {
        return "requested id = " + Integer.toString(requestedId);
    }

    @Override
    public void readFrom(InputStream is) throws InvalidProtocolException {
        try {
            DataInputStream in = new DataInputStream(is);
            requestedId= in.readInt();
        } catch (IOException e) {
            throw new InvalidProtocolException("Format error during reading request: " + e.getMessage());
        }
    }

    @Override
    public void writeTo(OutputStream os) throws IOException {
        DataOutputStream out = new DataOutputStream(os);
        out.writeInt(requestedId);
    }

    @Override
    public Query getQuery() {
        return new SourcesQuery();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SourcesRequest that = (SourcesRequest) o;

        return requestedId == that.requestedId;
    }

    @Override
    public int hashCode() {
        return requestedId;
    }

    @Override
    public <T> T dispatch(MessageHandler<T> handler) {
        return handler.handle(this);
    }
}
