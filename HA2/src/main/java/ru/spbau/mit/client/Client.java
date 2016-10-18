package ru.spbau.mit.client;

import ru.spbau.mit.io.Logger;
import ru.spbau.mit.io.LoggerFactory;
import ru.spbau.mit.net.Query;
import ru.spbau.mit.net.TypedSocket;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

import static ru.spbau.mit.io.Utils.createFile;
import static ru.spbau.mit.net.Query.GET_CMD;
import static ru.spbau.mit.net.Query.LIST_CMD;

public class Client {
    // Constants
    private static final String SERVER_ADDRESS = "localhost";

    private static final int SERVER_PORT = 8228;

    // Utility
    private static final Logger log = LoggerFactory.getDefaultLogger();

    private SocketChannel socketChannel;
    private TypedSocket typedSocket;

    public Client() { }

    public void connect() throws IOException {
        socketChannel = SocketChannel.open();
        typedSocket = new TypedSocket(socketChannel);

        InetSocketAddress serverAddress = new InetSocketAddress(SERVER_ADDRESS, SERVER_PORT);
        log.trace("Client ctor: connecting to server on " + serverAddress.toString() + "...");
        socketChannel.connect(serverAddress);
        log.trace("Client ctor: connected successfully!");
    }

    public ArrayList<String> getList() throws IOException {
        log.trace("Client: Querying list of files from server...");
        typedSocket.writeObject(new Query(LIST_CMD));
        log.trace("Client: Query sent successfully");

        log.trace("Client: Reading list of files...");
        Object result = typedSocket.readObject();
        log.trace("Client: Read successfully");
        return (ArrayList<String>) result;
    }

    public void getFile(String fileName) throws IOException {
        log.trace("Client: Querying file download from server...");
        typedSocket.writeObject(new Query(GET_CMD, fileName));
        log.trace("Client: Query sent successfully");

        log.trace("Client: waiting for response from server...");
        String response = typedSocket.readString();
        if (!response.equals("OK")) {
            throw new IOException("Error downloading file from server: " + response);
        }
        log.trace("Client: Server responded OK");

        log.trace("Client: Creating an empty file");
        File f = createFile(fileName + ".download");
        log.trace("Client: Empty file created");

        log.trace("Client: Downloading file...");
        downloadFile(f);
        log.trace("Client: File downloaded!");
    }

    private void downloadFile(File dst) throws IOException {
        FileChannel fc = FileChannel.open(dst.toPath(), StandardOpenOption.WRITE);
        ByteBuffer buffer = ByteBuffer.allocate(1000000);
        while (socketChannel.read(buffer) != -1) {
            buffer.flip();
            fc.write(buffer);
            buffer.clear();
        }
    }


    public void shutdown() throws IOException {
        socketChannel.close();
    }
}
