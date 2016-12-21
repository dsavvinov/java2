package wire;

import java.io.IOException;
import java.nio.ByteBuffer;

public class MessagesParser {
    private final Protocol protocol;
    private ByteBuffer buffer;

    public MessagesParser(int capacity, Protocol protocol) {
        buffer = ByteBuffer.allocate(capacity);
        this.protocol = protocol;
    }

    public synchronized void put(byte[] bytes) {
        buffer.put(bytes);
    }

    public synchronized WireMessages.Numbers get() {
        buffer.flip();
        buffer.mark();
        WireMessages.Numbers numbers;
        try {
            byte[] msgBytes = new byte[buffer.remaining()];
            buffer.get(msgBytes);
            numbers = protocol.fromBytes(msgBytes);
        } catch (IOException ignored) {
           buffer.reset();
           buffer.compact();
           return null;
        }

        buffer.compact();
        return numbers;
    }
}
