package core.server;

import database.DatabaseProvider;
import database.server.ServerDatabase;
import exceptions.InvalidProtocolException;
import io.Logger;
import net.Message;
import net.MessageHandler;
import net.Protocol;
import net.protocols.ClientServerProtocol;
import net.queries.requests.ListRequest;
import net.queries.requests.SourcesRequest;
import net.queries.requests.UpdateRequest;
import net.queries.requests.UploadRequest;
import net.queries.responses.ListResponse;
import net.queries.responses.SourcesResponse;
import net.queries.responses.UpdateResponse;
import net.queries.responses.UploadResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.stream.Collectors;

public class ClientHandler implements Runnable, MessageHandler<Message> {
    private final Socket clientSocket;
    private final Logger log;
    private final String prefix;    // for logging
    private final Protocol clientServerProtocol = new ClientServerProtocol();

    ClientHandler(Socket clientSocket, Logger log) {
        this.clientSocket = clientSocket;
        this.log = log;
        this.prefix = "[" + clientSocket.toString() + "]";
    }

    @Override
    public void run() {
       try {
            InputStream inputStream = clientSocket.getInputStream();
            OutputStream outputStream = clientSocket.getOutputStream();

            log.trace(prefix + " reading request");
            Message request = clientServerProtocol.readRequest(inputStream);
            log.trace(prefix + " got request: <" + request.toString() + ">");

            Message response = request.dispatch(this);

            log.trace(prefix + " got response: " + response.toString());
            clientServerProtocol.writeResponse(response, outputStream);
            log.trace(prefix + " sent response, closing connection");
        } catch (InvalidProtocolException e) {
            log.error("Protocol error while handling client <" + clientSocket.toString()
                    + ">: " + e.getMessage());
        } catch (IOException e) {
            log.error("IO error: " + e.toString());
        }
    }


    @Override
    public Message handle(ListRequest listRequest) {
        ServerDatabase db = DatabaseProvider.getServerDB();
        ListResponse responseData = new ListResponse();

        List<ListResponse.ListResponseItem> files = db
                .listAllFiles()
                .stream()
                .map(
                        it -> new ListResponse.ListResponseItem(
                                it.getId(),
                                it.getName(),
                                it.getSize()
                        ))
                .collect(Collectors.toList());

        responseData.addAll(files);

        return responseData;
    }

    @Override
    public Message handle(SourcesRequest sourcesRequest) {
        ServerDatabase db = DatabaseProvider.getServerDB();

        List<SourcesResponse.Source> sources = db
                .listAllSeedsOf(sourcesRequest.getRequestedId())
                .stream()
                .map(userEntity -> new SourcesResponse.Source(
                                userEntity.getPort(),
                                userEntity.getAddress()
                        )
                )
                .collect(Collectors.toList());

        return new SourcesResponse(sources);
    }

    @Override
    public Message handle(UpdateRequest updateRequest) {
        int port = updateRequest.getClientPort();
        int[] ids = updateRequest.getIds();

        ServerDatabase db = DatabaseProvider.getServerDB();
        db.updateFilesForUser(ids, clientSocket.getInetAddress().getHostAddress(), port);

        return new UpdateResponse(true);
    }

    @Override
    public Message handle(UploadRequest uploadRequest) {
        ServerDatabase db = DatabaseProvider.getServerDB();

        int id = db.uploadFile(uploadRequest.getName(), uploadRequest.getSize());
        return new UploadResponse(id);
    }
}
