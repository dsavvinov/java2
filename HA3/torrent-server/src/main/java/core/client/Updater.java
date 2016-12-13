package core.client;

import database.DatabaseProvider;
import database.FileEntity;
import database.client.ClientDatabase;
import exceptions.InvalidProtocolException;
import io.Logger;
import net.Message;
import net.Protocol;
import net.queries.UpdateQuery;
import net.queries.requests.UpdateRequest;
import net.queries.responses.UpdateResponse;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

import static utils.Constants.SERVER_ADDRESS;
import static utils.Constants.SERVER_PORT;
import static utils.Constants.UPDATE_SOFT_TIMEOUT;

public class Updater {
    private volatile boolean shutdown = false;
    private final Logger log;
    private final short clientPort;
    private final Protocol protocol;
    private final ClientDatabase db;
    private Thread updateThread;

    public Updater(Logger log, short clientPort, Protocol protocol, ClientDatabase db) {
        this.log = log;
        this.clientPort = clientPort;
        this.protocol = protocol;
        this.db = db;
    }

    public void shutdown() {
        shutdown = true;
        updateThread.interrupt();

        while (updateThread.isAlive()) {
            try {
                updateThread.join();
            } catch (InterruptedException ignored) { }
        }
    }

    public void start() {
        updateThread = new Thread(() -> {
            while (!shutdown) {
                update();
                try {
                    Thread.sleep(UPDATE_SOFT_TIMEOUT);
                } catch (InterruptedException e) {
                    // just ignore spurious wake-ups;
                    // rare keep-alive timeouts won't harm too much anyway
                    return;
                }
            }
            log.trace("[Updater] Shutting down");
        });

        updateThread.start();
    }

    private void update() {
        log.trace("[Updater] Updating");
        try (Socket serverSocket = new Socket(SERVER_ADDRESS, SERVER_PORT)) {
            serverSocket.setSoTimeout(2000);

            // Get ids of seeded files
            List<FileEntity> seededFiles = db.listSeededFiles();
            int[] ids = seededFiles.stream().mapToInt(FileEntity::getId).toArray();

            // Form update-request
            Message updateRequest = new UpdateRequest(clientPort, ids);

            // Send request
            log.trace("[Updater] writing request <" + updateRequest.toString() + ">");
            protocol.writeRequest(updateRequest, serverSocket.getOutputStream());
            log.trace("[Updater] sent request, waiting for response...");

            // Get response
            Message response = protocol.readResponse(new UpdateQuery(), serverSocket.getInputStream());
            log.trace("[Updater] got response");

            // Check response status
            boolean status = ((UpdateResponse) response).getStatus();
            if (status) {
                log.info("[Updater] Update sent successfully");
            } else {
                log.error("[Updater] Error sending update: rejected by server");
            }

        } catch (UnknownHostException e) {
            log.error("Error establishing connection to server: host <" + SERVER_ADDRESS
                    + ":" + SERVER_PORT + "> is unknown");
        } catch (SocketTimeoutException e) {
            log.error("Timeout during interaction with server");
        } catch (IOException e) {
            log.error("Error during conversation with server: <" + e.getMessage() + ">");
        } catch (InvalidProtocolException e) {
            log.error(e.getMessage());
        }
    }
}
