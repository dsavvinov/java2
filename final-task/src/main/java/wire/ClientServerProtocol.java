package wire;

import java.io.*;

import static wire.WireMessages.Numbers;

public class ClientServerProtocol implements Protocol {
    @Override
    public Numbers readMessage(InputStream is) throws IOException {
        return Numbers.parseDelimitedFrom(is);
    }

    @Override
    public void writeMessage(Numbers msg, OutputStream os) throws IOException {
        msg.writeDelimitedTo(os);
    }

    @Override
    public byte[] toBytes(Numbers msg) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(msg.getSerializedSize() + 100);
        msg.writeDelimitedTo(byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public Numbers fromBytes(byte[] bytes) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        return Numbers.parseDelimitedFrom(bais);
    }
}
