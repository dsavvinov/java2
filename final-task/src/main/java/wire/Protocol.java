package wire;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static wire.WireMessages.Numbers;

public interface Protocol {
    Numbers readMessage(InputStream is) throws IOException;
    void writeMessage(Numbers msg, OutputStream os) throws IOException;
    byte[] toBytes(Numbers msg) throws IOException;
    Numbers fromBytes(byte[] bytes) throws IOException;
}
