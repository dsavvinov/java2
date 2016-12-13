package core.p2p;

import database.client.ClientDatabase;
import io.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PeerServer {
    private volatile boolean shutdown = false;
    private final Logger log;
    private final String PREFIX = "[PeerServer]";
    private final ExecutorService threadPool = Executors.newFixedThreadPool(100);
    private final String rootDir;
    private final short clientPort;
    private Thread serverThread;
    private ServerSocketChannel serverSocket;
    private final ClientDatabase clientDatabase;

    public PeerServer(short clientPort, String rootDir, Logger log, ClientDatabase clientDatabase) {
        this.log = log;
        this.rootDir = rootDir;
        this.clientPort = clientPort;
        this.clientDatabase = clientDatabase;
    }

    public void start() throws IOException {
        serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress(clientPort));
        serverSocket.socket().setSoTimeout(1000);

        serverThread = new Thread(this::acceptLoop);
        serverThread.start();
    }

    public void shutdown() {
        shutdown = true;
        serverThread.interrupt();
        while(serverThread.isAlive()) {
            try {
                serverThread.join();
            } catch (InterruptedException ignored) { }
        }

        // Cancel all awaiting tasks
        threadPool.shutdownNow();

        // Await finishing all active tasks, and shutdown abruptly if it takes too long;
        try {
            threadPool.awaitTermination(2, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) { }
    }

    private void acceptLoop() {
        while (!shutdown) {
            try {
                SocketChannel peerSocket = serverSocket.accept();
                threadPool.execute(new PeerHandler(peerSocket, log, rootDir, clientDatabase));
            } catch (ClosedByInterruptException ignored) {
            } catch (IOException e) {
                log.error(PREFIX + " IO Error: " + e.getMessage());
            }
        }
    }
}
