package net.queries.responses;

import exceptions.InvalidProtocolException;
import net.Message;
import net.MessageHandler;
import net.Query;
import net.queries.UpdateQuery;

import java.io.*;

public class UpdateResponse implements Message {
    public boolean getStatus() {
        return status;
    }

    private boolean status;

    public UpdateResponse() { }

    public UpdateResponse(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return Boolean.toString(status);
    }

    @Override
    public void readFrom(InputStream is) throws InvalidProtocolException {
        DataInputStream in = new DataInputStream(is);
        try {
            status = in.readBoolean();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeTo(OutputStream os) throws IOException {
        DataOutputStream out = new DataOutputStream(os);
        out.writeBoolean(status);
    }

    @Override
    public Query getQuery() {
        return new UpdateQuery();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UpdateResponse that = (UpdateResponse) o;

        return status == that.status;
    }

    @Override
    public int hashCode() {
        return (status ? 1 : 0);
    }

    @Override
    public <T> T dispatch(MessageHandler<T> handler) {
        return handler.handle(this);
    }

}
