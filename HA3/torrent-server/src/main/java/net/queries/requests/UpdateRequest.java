package net.queries.requests;

import exceptions.InvalidProtocolException;
import net.MessageHandler;
import net.Query;
import net.Message;
import net.queries.UpdateQuery;
import net.queries.UploadQuery;

import java.io.*;
import java.util.Arrays;

public class UpdateRequest implements Message {
    private short clientPort;
    private int[] ids;

    public short getClientPort() {
        return clientPort;
    }

    public int[] getIds() {
        return ids;
    }

    public UpdateRequest() { }

    public UpdateRequest(short clientPort, int[] ids) {
        this.clientPort = clientPort;
        this.ids = ids;
    }

    @Override
    public String toString() {
        return "port = " + Integer.toString(clientPort) + ", ids = " + Arrays.toString(ids);
    }

    @Override
    public void readFrom(InputStream is) throws InvalidProtocolException {
        DataInputStream in = new DataInputStream(is);
        try {
            clientPort = in.readShort();
            int count = in.readInt();
            ids = new int[count];
            for (int i = 0; i < count; i++) {
                ids[i] = in.readInt();
            }
        } catch (IOException e) {
            throw new InvalidProtocolException("Format error during reading request: " + e.getMessage());
        }
    }

    @Override
    public void writeTo(OutputStream os) throws IOException {
        DataOutputStream out = new DataOutputStream(os);
        out.writeShort(clientPort);
        out.writeInt(ids.length);
        for (int id : ids) {
            out.writeInt(id);
        }
    }

    @Override
    public Query getQuery() {
        return new UpdateQuery();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UpdateRequest that = (UpdateRequest) o;

        if (clientPort != that.clientPort) return false;
        return Arrays.equals(ids, that.ids);
    }

    @Override
    public int hashCode() {
        int result = (int) clientPort;
        result = 31 * result + Arrays.hashCode(ids);
        return result;
    }

    @Override
    public <T> T dispatch(MessageHandler<T> handler) {
        return handler.handle(this);
    }
}
