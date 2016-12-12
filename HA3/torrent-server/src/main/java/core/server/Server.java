package core.server;

import io.Logger;
import io.StandardLogger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static utils.Constants.SERVER_PORT;

public class Server {
    public volatile boolean shouldStop = false;     // for terminating
    private static final int WORKERS_COUNT = 1000;

    private final Logger log;
    private final ExecutorService threadPool =  Executors.newFixedThreadPool(WORKERS_COUNT);
    private ServerSocket serverSocket;

    public Server(Logger log) {
        this.log = log;
    }

    public Server() {
        log = StandardLogger.getInstance();
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(SERVER_PORT);
        serverSocket.setSoTimeout(1000);    // don't block for too much to be able terminate on demand
        while (true) {
            Socket clientSocket;
            try {
                clientSocket = serverSocket.accept();
            } catch (SocketTimeoutException ignored) {
                if (shouldStop) {
                    serverSocket.close();
                    break;
                } else {
                    continue;
                }
            }

            log.info("Accepted: " + clientSocket.toString());
            threadPool.execute(new ClientHandler(clientSocket, log));
        }
    }
}
