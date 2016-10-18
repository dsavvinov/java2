package ru.spbau.mit.server;


import ru.spbau.mit.io.Logger;
import ru.spbau.mit.io.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    // Constants
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8228;

    // Utility
    private static final Logger log = LoggerFactory.getDefaultLogger();

    private final ExecutorService threadPool = Executors.newFixedThreadPool(4);

    public void start() throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        serverSocketChannel.socket().bind(new InetSocketAddress(SERVER_PORT));
        Storage.initHere();
        try {
            while (!Thread.interrupted()) {
                log.trace("Server: listening for connections...");
                SocketChannel clientSocketChannel = serverSocketChannel.accept();
                log.trace("Server: accepted connection from <" +
                        clientSocketChannel.getRemoteAddress().toString() + ">");
                threadPool.execute(new ClientHandler(log, clientSocketChannel));
            }
        } finally {
            serverSocketChannel.close();
        }

    }
}
