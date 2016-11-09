package core.client;

import database.FileEntity;
import database.client.ClientDatabase;
import database.DatabaseProvider;
import exceptions.InvalidProtocolException;
import exceptions.WrongArgumentException;
import io.Logger;
import io.StandardLogger;
import net.ClientServerProtocol;
import net.requests.Request;
import net.requests.RequestType;
import net.requests.UpdateRequestData;
import net.responses.Response;
import net.responses.UpdateResponseData;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

import static utils.Constants.*;

public class Updater implements Runnable {
    private static final Logger log = StandardLogger.getInstance();
    private final Client parent;

    public Updater(Client parent) {
        this.parent = parent;
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
            ClientDatabase db = DatabaseProvider.getClientDB();
            List<FileEntity> seededFiles = db.listSeededFiles();
            int[] ids = seededFiles.stream().mapToInt(FileEntity::getId).toArray();

            Request updateRequest = new Request(
                    RequestType.UPDATE, new UpdateRequestData(parent.clientPort, ids)
            );

            log.trace("[Updater] writing request <" + updateRequest.toString() + ">");
            ClientServerProtocol.writeRequest(updateRequest, serverSocket.getOutputStream());
            log.trace("[Updater] sent request, waiting for response...");
            Response response = ClientServerProtocol.readResponse(RequestType.UPDATE, serverSocket.getInputStream());
            log.trace("[Updater] got response");
            boolean status = ((UpdateResponseData) response.getData()).getStatus();
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
        } catch (WrongArgumentException | InvalidProtocolException e) {
            log.error(e.getMessage());
        }
    }
}
