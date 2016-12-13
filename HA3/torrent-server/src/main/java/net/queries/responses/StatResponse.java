package net.queries.responses;

import exceptions.InvalidProtocolException;
import net.Message;
import net.MessageHandler;
import net.Query;
import net.queries.StatQuery;

import java.io.*;
import java.util.Arrays;

public class StatResponse implements Message {
    private int[] parts;

    public StatResponse() { }

    public StatResponse(int[] parts) {
        this.parts = parts;
    }

    public int[] getParts() {
        return parts;
    }

    @Override
    public void readFrom(InputStream is) throws InvalidProtocolException {
        DataInputStream in = new DataInputStream(is);
        try {
            int count = in.readInt();
            parts = new int[count];

            for (int i = 0; i < count; i++) {
                parts[i] = in.readInt();
            }
        } catch (IOException e) {
            throw new InvalidProtocolException("Format error during reading request: " + e.getMessage());
        }
    }

    @Override
    public void writeTo(OutputStream os) throws IOException {
        DataOutputStream out = new DataOutputStream(os);
        out.writeInt(parts.length);
        for (int part : parts) {
            out.writeInt(part);
        }
    }

    @Override
    public Query getQuery() {
        return new StatQuery();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StatResponse that = (StatResponse) o;

        return Arrays.equals(parts, that.parts);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(parts);
    }

    @Override
    public <T> T dispatch(MessageHandler<T> handler) {
        return handler.handle(this);
    }

    @Override
    public String toString() {
        return "StatResponse{" +
                "parts=" + Arrays.toString(parts) +
                '}';
    }
}
