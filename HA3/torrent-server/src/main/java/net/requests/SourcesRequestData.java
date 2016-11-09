package net.requests;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class SourcesRequestData {
    private final int requestedId;

    public int getRequestedId() {
        return requestedId;
    }

    public SourcesRequestData(int requestedId) {
        this.requestedId = requestedId;
    }

    @Override
    public String toString() {
        return "requested id = " + Integer.toString(requestedId);
    }
}
