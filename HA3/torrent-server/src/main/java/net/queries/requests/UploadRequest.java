package net.queries.requests;

import exceptions.InvalidProtocolException;
import net.MessageHandler;
import net.Query;
import net.Message;
import net.queries.UploadQuery;

import java.io.*;

public class UploadRequest implements Message {
    private String name;
    private long size;

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public UploadRequest() { }

    public UploadRequest(String name, long size) {
        this.name = name;
        this.size = size;
    }

    @Override
    public String toString() {
        return "name = " + name + ", size = " + Long.toString(size);
    }

    @Override
    public void readFrom(InputStream is) throws InvalidProtocolException {
        DataInputStream in = new DataInputStream(is);
        try {
            name = in.readUTF();
            size = in.readLong();
        } catch (IOException e) {
            throw new InvalidProtocolException("Format error during reading request: " + e.getMessage());
        }
    }

    @Override
    public void writeTo(OutputStream os) throws IOException {
        DataOutputStream out = new DataOutputStream(os);
        out.writeUTF(name);
        out.writeLong(size);
    }

    @Override
    public Query getQuery() {
        return new UploadQuery();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UploadRequest that = (UploadRequest) o;

        if (size != that.size) return false;
        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (int) (size ^ (size >>> 32));
        return result;
    }

    @Override
    public <T> T dispatch(MessageHandler<T> handler) {
        return handler.handle(this);
    }
}
