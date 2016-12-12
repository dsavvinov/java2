package core.p2p;

import core.client.Client;
import io.Logger;
import io.StandardLogger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Peer implements Runnable {
    private final Client parent;
    private Logger log = StandardLogger.getInstance();
    private final String PREFIX = "[Peer-server]";
    private final ExecutorService threadPool = Executors.newFixedThreadPool(100);
    private final String rootDir;
    public Peer(Client client, Logger log, String rootDir) {
        parent = client;
        this.log = log;
        this.rootDir = rootDir;
    }

    @Override
    public void run() {
        ServerSocketChannel serverSocket;
        try {
            serverSocket = ServerSocketChannel.open();
            serverSocket.bind(new InetSocketAddress(parent.clientPort));
            serverSocket.socket().setSoTimeout(1000);
        } catch (SocketException e) {
            log.error(PREFIX + " Can't set socket timeout: " + e.getMessage());
            throw new RuntimeException(e);
        } catch (IOException e) {
            log.error(PREFIX + " Can't open peer-server socket: " + e.getMessage());
            throw new RuntimeException(e);
        }


        while (!parent.shutdown) {
            try {
                SocketChannel peerSocket = serverSocket.accept();
                threadPool.execute(new PeerHandler(peerSocket, log, rootDir));
            } catch (ClosedByInterruptException ignored) {
            }
            catch (IOException e) {
                log.error(PREFIX + " IO Error: " + e.getMessage());
            }
        }

        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
