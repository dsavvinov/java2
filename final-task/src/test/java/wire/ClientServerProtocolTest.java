package wire;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;
import static wire.WireMessages.*;

public class ClientServerProtocolTest {
    private static final Random rng = new Random(42);
    private static final int ARRAY_MAX_SIZE = 10000;
    private static final int MAX_MESSAGES_COUNT = 100;
    private static final Protocol protocol = new ClientServerProtocol();

    private void assertReadWriteIdentity(List<Numbers> messages) throws IOException {
        int totalSize = 0;
        for (Numbers msg : messages) {
            totalSize += msg.getSerializedSize();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream(totalSize);

        for (Numbers msg: messages) {
            protocol.writeMessage(msg, baos);
        }

        byte[] serializedMessage = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(serializedMessage);

        for (int i = 0; i < messages.size(); i++) {
            Numbers readMsg = protocol.readMessage(bais);
            assertEquals(messages.get(i).getItemsList(), readMsg.getItemsList());
        }
    }

    @Test
    public void testTrivialMessage() throws IOException {
        Numbers.Builder builder = Numbers.newBuilder();
        builder.addItems(1);
        builder.addItems(2);
        builder.addItems(3);

        Numbers msg = builder.build();
        assertReadWriteIdentity(Collections.singletonList(msg));
    }

    @Test
    public void testTwoMessages() throws IOException {
        Numbers.Builder builder = Numbers.newBuilder();
        builder.addItems(1);
        builder.addItems(2);
        builder.addItems(3);

        Numbers msg1 = builder.build();

        builder.clear();
        builder.addItems(-1);
        builder.addItems(-2);
        builder.addItems(-3);

        Numbers msg2 = builder.build();

        assertReadWriteIdentity(Arrays.asList(msg1, msg2));
    }

    @Test
    public void stressTest() throws IOException {
        List<Numbers> messages = new ArrayList<>();

        int messagesCount = rng.nextInt(MAX_MESSAGES_COUNT);

        for (int i = 0; i < messagesCount; i++) {
            Numbers.Builder builder = Numbers.newBuilder();
            int arrayLength = rng.nextInt(ARRAY_MAX_SIZE);
            for (int j = 0; j < arrayLength; j++) {
                builder.addItems(rng.nextInt());
            }
            messages.add(builder.build());
        }

        assertReadWriteIdentity(messages);
    }
}