package net.requests;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Request {
    private final RequestType type;
    private final Object data;

    public Request(RequestType type, Object data) {
        this.type = type;
        this.data = data;
    }

    public RequestType getType() {
        return type;
    }

    public Object getData() {
        return data;
    }

    public void writeTo(OutputStream os) throws IOException {
        DataOutputStream out = new DataOutputStream(os);
        out.write(type.getId());
    }

    @Override
    public String toString() {
        return "Type = " +
                type.toString() +
                ", " +
                data.toString();
    }
}
