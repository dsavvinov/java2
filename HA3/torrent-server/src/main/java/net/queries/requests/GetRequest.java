package net.queries.requests;

import exceptions.InvalidProtocolException;
import net.MessageHandler;
import net.Query;
import net.Message;
import net.queries.GetQuery;

import java.io.*;

public class GetRequest implements Message {
    private int id;
    private int part;

    public GetRequest() { }

    public GetRequest(int id, int part) {
        this.id = id;
        this.part = part;
    }

    public int getId() {
        return id;
    }

    public int getPart() {
        return part;
    }

    @Override
    public void readFrom(InputStream is) throws InvalidProtocolException {
        DataInputStream in = new DataInputStream(is);
        try {
            id = in.readInt();
            part = in.readInt();
        } catch (IOException e) {
            throw new InvalidProtocolException("Format error during reading request: " + e.getMessage());
        }
    }

    @Override
    public void writeTo(OutputStream os) throws IOException {
        DataOutputStream out = new DataOutputStream(os);
        out.writeInt(id);
        out.writeInt(part);
    }

    @Override
    public Query getQuery() {
        return new GetQuery();
    }

    @Override
    public <T> T dispatch(MessageHandler<T> handler) {
        return handler.handle(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GetRequest that = (GetRequest) o;

        if (id != that.id) return false;
        return part == that.part;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + part;
        return result;
    }
}
