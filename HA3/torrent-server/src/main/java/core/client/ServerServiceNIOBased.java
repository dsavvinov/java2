package core.client;

import database.FileEntity;
import database.client.ClientDatabase;
import exceptions.InvalidProtocolException;
import io.Logger;
import io.StandardLogger;
import net.Message;
import net.Protocol;
import net.queries.requests.ListRequest;
import net.queries.requests.SourcesRequest;
import net.queries.requests.UpdateRequest;
import net.queries.requests.UploadRequest;
import net.queries.responses.ListResponse;
import net.queries.responses.SourcesResponse;
import net.queries.responses.UpdateResponse;
import net.queries.responses.UploadResponse;

import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static utils.Constants.SERVER_ADDRESS;
import static utils.Constants.SERVER_PORT;

public class ServerServiceNIOBased implements ServerService {
    public final short clientPort;
    private Logger log = StandardLogger.getInstance();
    private final Protocol protocol;
    private final ClientDatabase db;

    public ServerServiceNIOBased(short clientPort, Protocol protocol, ClientDatabase db) {
        this.clientPort = clientPort;
        this.protocol = protocol;
        this.db = db;
    }

    public ServerServiceNIOBased(short clientPort, Protocol protocol, ClientDatabase db, Logger log) {
        this.clientPort = clientPort;
        this.log = log;
        this.protocol = protocol;
        this.db = db;
    }

    @Override
    public UploadResponse upload(Path absolutePath) throws IOException, InvalidProtocolException {
        long size = absolutePath.toFile().length();
        String name = absolutePath.getFileName().toString();

        Message request = new UploadRequest(name, size);
        UploadResponse response = (UploadResponse) interact(request);

        FileEntity file = new FileEntity(response.getId(), name, size);
        file.setLocalPath(absolutePath.toString());
        db.addFile(file);
        db.addAllPartsOfFile(file);

        return response;
    }

    @Override
    public SourcesResponse sources(int fileId) throws IOException, InvalidProtocolException {
        Message r = new SourcesRequest(fileId);
        return (SourcesResponse) interact(r);
    }

    @Override
    public ListResponse list() throws IOException, InvalidProtocolException {
        Message request = new ListRequest();
        return (ListResponse) interact(request);
    }

    @Override
    public UpdateResponse update() throws IOException, InvalidProtocolException {
        List<FileEntity> seededFiles = db.listSeededFiles();
        int[] ids = seededFiles.stream().mapToInt(FileEntity::getId).toArray();

        Message request = new UpdateRequest(clientPort, ids);

        return (UpdateResponse) interact(request);
    }

    private Message interact(Message request) throws IOException, InvalidProtocolException {
        try (Socket serverSocket = new Socket(SERVER_ADDRESS, SERVER_PORT)) {
            log.trace("Sending request <" + request.toString() + ">");
            protocol.writeRequest(request, serverSocket.getOutputStream());
            log.trace("Sent successfully, waiting for response...");

            Message response = protocol.readResponse(request.getQuery(), serverSocket.getInputStream());
            log.trace("Got response <" + response.toString() + ">");

            log.trace("Closing connection");
            return response;
        }
    }
}
