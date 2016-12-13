package net.queries.responses;

import exceptions.InvalidProtocolException;
import net.Message;
import net.MessageHandler;
import net.Query;
import net.queries.UploadQuery;

import java.io.*;

public class UploadResponse implements Message {
    public int getId() {
        return id;
    }

    private int id;

    public UploadResponse() { }

    public UploadResponse(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return Integer.toString(id);
    }

    @Override
    public void readFrom(InputStream is) throws InvalidProtocolException {
        DataInputStream in = new DataInputStream(is);
        try {
            id = in.readInt();
        } catch (IOException e) {
            throw new InvalidProtocolException("Format error during reading request: " + e.getMessage());
        }
    }

    @Override
    public void writeTo(OutputStream os) throws IOException {
        DataOutputStream out = new DataOutputStream(os);
        out.writeInt(id);
    }

    @Override
    public Query getQuery() {
        return new UploadQuery();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UploadResponse that = (UploadResponse) o;

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
