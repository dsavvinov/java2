package net;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

public class Util {
    public static void assertWriteReadIdentity(Protocol protocol, Message msg, boolean isRequest) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream(10000);
        if (isRequest) {
            protocol.writeRequest(msg, os);
        } else {
            protocol.writeResponse(msg, os);
        }

        InputStream is = new ByteArrayInputStream(os.toByteArray());

        Message readMsg;
        if (isRequest) {
            readMsg = protocol.readRequest(is);
        } else {
            readMsg = protocol.readResponse(msg.getQuery(), is);
        }

        assertEquals(msg, readMsg);
    }
}
