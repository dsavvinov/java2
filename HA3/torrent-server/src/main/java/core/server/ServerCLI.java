package core.server;

import io.Logger;
import io.StandardLogger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static utils.Constants.SERVER_PORT;


public class ServerCLI {
    private static final Logger log = StandardLogger.getInstance();

    public static void main(String[] args) {
        try {
            Server server = new Server();
            server.start();
        } catch (IOException e) {
            log.error("Error accepting connection: " + e.getMessage());
        }
    }
}
