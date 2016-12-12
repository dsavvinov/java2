package core.client;

import database.DatabaseProvider;
import database.FileEntity;
import database.client.ClientDatabase;
import exceptions.InvalidProtocolException;
import io.Logger;
import io.StandardLogger;
import net.Message;
import net.protocols.ClientServerProtocol;
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

public class Updater implements Runnable {
    private Logger log = StandardLogger.getInstance();
    private final Client parent;
    private final ClientServerProtocol clientServerProtocol = new ClientServerProtocol();

    public Updater(Client parent, Logger log) {
        this.parent = parent;
        this.log = log;
    }

    @Override
    public void run() {
        while (true) {
            update();
            try {
                Thread.sleep(4 * 50 * 1000);
            } catch (InterruptedException e) {
                if (parent.shutdown) {
                    log.trace("[Updater] Shutting down");
                    return;
                }
                // just ignore spurious wake-ups;
                // rare keep-alive timeouts won't harm too much anyway
            }
        }
    }

    private void update() {
        log.trace("[Updater] Updating");
        try (Socket serverSocket = new Socket(SERVER_ADDRESS, SERVER_PORT)) {
            serverSocket.setSoTimeout(2000);

            // Get ids of seeded files
            ClientDatabase db = DatabaseProvider.getClientDB();
            List<FileEntity> seededFiles = db.listSeededFiles();
            int[] ids = seededFiles.stream().mapToInt(FileEntity::getId).toArray();

            // Form update-request
            Message updateRequest = new UpdateRequest(parent.clientPort, ids);

            // Send request
            log.trace("[Updater] writing request <" + updateRequest.toString() + ">");
            clientServerProtocol.writeRequest(updateRequest, serverSocket.getOutputStream());
            log.trace("[Updater] sent request, waiting for response...");

            // Get response
            Message response = clientServerProtocol.readResponse(new UpdateQuery(), serverSocket.getInputStream());
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
