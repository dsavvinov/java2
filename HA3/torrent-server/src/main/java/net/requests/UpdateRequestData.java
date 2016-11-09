package net.requests;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

public class UpdateRequestData {
    private final short clientPort;
    private final int[] ids;

    public short getClientPort() {
        return clientPort;
    }

    public int[] getIds() {
        return ids;
    }

    public UpdateRequestData(short clientPort, int[] ids) {
        this.clientPort = clientPort;
        this.ids = ids;
    }

    @Override
    public String toString() {
        return "port = " + Integer.toString(clientPort) + ", ids = " + Arrays.toString(ids);
    }
}
