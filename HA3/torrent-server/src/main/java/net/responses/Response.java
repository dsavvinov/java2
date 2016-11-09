package net.responses;

import net.requests.RequestType;

public class Response {
    public RequestType getType() {
        return type;
    }

    public Object getData() {
        return data;
    }

    private final RequestType type;
    private final Object data;

    public Response(RequestType type, Object data) {
        this.type = type;
        this.data = data;
    }

    @Override
    public String toString() {
        return "Type = " +
                type.toString() +
                ", " +
                data.toString();
    }
}
