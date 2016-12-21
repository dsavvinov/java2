package wire;

import org.junit.BeforeClass;
import org.junit.Test;
import util.Parameters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.*;
import static wire.WireMessages.*;

public class MessagesParserTest {
    private static Numbers msg1;
    private static Numbers msg2;

    @BeforeClass
    public static void prepareClass() {
        Parameters.ARRAY_SIZE = 100;
        Numbers.Builder builder = Numbers.newBuilder();
        builder.addItems(1);
        builder.addItems(2);
        builder.addItems(3);
        msg1 = builder.build();

        builder.clear();
        builder.addItems(-1);
        builder.addItems(-2);
        builder.addItems(-3);
        msg2 = builder.build();
    }

    @Test
    public void singleMessage() throws IOException {
        MessagesParser parser = new MessagesParser(10000, protocol);

        Numbers.Builder builder = Numbers.newBuilder();
        builder.addItems(1);
        builder.addItems(2);
        builder.addItems(3);
        Numbers msg = builder.build();
        writeMessageDelimitedTo(parser, msg);

        Numbers numbers = parser.get();
        assertEquals(msg.getItemsList(), numbers.getItemsList());
    }

    @Test
    public void twoMessages() throws IOException {
        MessagesParser parser = new MessagesParser(10000, protocol);

        writeMessageDelimitedTo(parser, msg1);
        writeMessageDelimitedTo(parser, msg2);

        Numbers numbers1 = parser.get();
        Numbers numbers2 = parser.get();

        assertEquals(msg1.getItemsList(), numbers1.getItemsList());
        assertEquals(msg2.getItemsList(), numbers2.getItemsList());
    }

    @Test
    public void incompleteMessage() throws IOException {
        MessagesParser parser = new MessagesParser(10000, protocol);

        byte[] msg1Bytes = getDelimitedBytes(msg1);
        byte[] msg2Bytes = getDelimitedBytes(msg2);
        byte[] msg2firstHalf  = Arrays.copyOfRange(msg2Bytes, 0, msg2Bytes.length / 2);
        byte[] msg2secondHalf = Arrays.copyOfRange(msg2Bytes, msg2Bytes.length / 2, msg2Bytes.length);

        parser.put(msg1Bytes);
        parser.put(msg2firstHalf);

        Numbers numbers1 = parser.get();
        assertEquals(msg1.getItemsList(), numbers1.getItemsList());

        Numbers failedRead = parser.get();
        assertEquals(null, failedRead);

        parser.put(msg2secondHalf);
        Numbers numbers2 = parser.get();
        assertEquals(msg2.getItemsList(), numbers2.getItemsList());
    }

    private void writeMessageDelimitedTo(MessagesParser parser, Numbers msg) throws IOException {
        parser.put(getDelimitedBytes(msg));
    }

    private byte[] getDelimitedBytes(Numbers msg) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        msg.writeDelimitedTo(baos);
        return baos.toByteArray();
    }
}