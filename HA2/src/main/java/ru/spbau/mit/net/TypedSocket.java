package ru.spbau.mit.net;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.spbau.mit.io.Logger;
import ru.spbau.mit.io.LoggerFactory;
import ru.spbau.mit.server.Item;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.List;

public class TypedSocket {
    private static final CharsetEncoder encoder = Charset.forName("UTF-16").newEncoder();
    private static final CharsetDecoder decoder = Charset.forName("UTF-16").newDecoder();
    private static final Logger log = LoggerFactory.getDefaultLogger();
    private static final String END_OF_MESSAGE = "$END$";
    private final SocketChannel socketChannel;

    public TypedSocket(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public void writeString(String msg) throws IOException {
        try {
            socketChannel.write(encoder.encode(CharBuffer.wrap(msg + END_OF_MESSAGE)));
        } catch (CharacterCodingException e) {
            throw new IOException("Error encoding message <" + msg + "> " +
                    "with charset <" + encoder.charset().toString() + ">");
        }
    }

    public String readString() throws IOException {
        StringBuilder result = new StringBuilder();
        ByteBuffer buffer = ByteBuffer.allocate(10);
        while (socketChannel.read(buffer) > 0) {
            buffer.flip();
            result.append(decoder.decode(buffer));
            if (result.toString().endsWith(END_OF_MESSAGE)) break;
            buffer.clear();
        }
        String resultStr = result.toString();

        if (!resultStr.endsWith(END_OF_MESSAGE)) {
            throw new IOException("Unexpected end of message <" + resultStr + ">");
        }

        return resultStr.substring(0, result.length() - END_OF_MESSAGE.length());
    }


    public void writeObject(Object obj) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String objAsJSON = mapper.writeValueAsString(obj);
        writeString(objAsJSON);
    }

    public Object readObject() throws IOException {
        return readObject(Object.class);
    }

    public <T> Object readObject(Class<T> clazz) throws IOException {
        String objAsJSON = readString();
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(objAsJSON, clazz);
    }

    public List<Item> readListOfItems() throws IOException {
        String objAsJSON = readString();
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(objAsJSON, new TypeReference<List<Item>>(){});
    }
}
