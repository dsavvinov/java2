package core.server;

import database.DatabaseProvider;
import database.server.ServerDatabase;
import exceptions.InvalidProtocolException;
import exceptions.WrongArgumentException;
import io.Logger;
import net.ClientServerProtocol;
import net.requests.*;
import net.responses.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.stream.Collectors;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final Logger log;
    private final String prefix;    // for logging

    public ClientHandler(Socket clientSocket, Logger log) {
        this.clientSocket = clientSocket;
        this.log = log;
        this.prefix = "[" + clientSocket.toString() + "]";
    }

    @Override
    public void run() {
        try {
            InputStream inputStream;
            OutputStream outputStream;
            try {
                inputStream = clientSocket.getInputStream();
                outputStream = clientSocket.getOutputStream();
            } catch (IOException e) {
                log.error("Error working with client socket: " + e.getMessage());
                return;
            }

            log.trace(prefix + " reading request");
            Request request = ClientServerProtocol.readRequest(inputStream);
            log.trace(prefix + " got request: <" + request.toString() + ">");
            Response response = null;
            switch (request.getType()) {
                case LIST:
                    response = getListResponse((ListRequestData) request.getData());
                    break;
                case SOURCES:
                    response = getSourcesResponse((SourcesRequestData) request.getData());
                    break;
                case UPDATE:
                    response = getUpdateResponse((UpdateRequestData) request.getData(),
                            clientSocket.getInetAddress().getHostAddress());
                    break;
                case UPLOAD:
                    response = getUploadResponse((UploadRequestData) request.getData());
                    break;
                default:
                    log.error("Wrong request type");
                    return;
            }

            log.trace(prefix + " got response: " + response.toString());
            ClientServerProtocol.writeResponse(response, outputStream);
            log.trace(prefix + " sent response, closing connection");
        } catch (InvalidProtocolException e) {
            log.error("Protocol error while handling client <" + clientSocket.toString()
                    + ">: " + e.getMessage());
        } catch (WrongArgumentException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error("IO error: " + e.toString());
        }
    }

    public Response getUploadResponse(UploadRequestData data) {
        ServerDatabase db = DatabaseProvider.getServerDB();

        int id = db.uploadFile(data.getName(), data.getSize());
        return new Response(RequestType.UPLOAD, new UploadResponseData(id));
    }

    public Response getUpdateResponse(UpdateRequestData data, String inetAddress) {
        ServerDatabase db = DatabaseProvider.getServerDB();

        int port = data.getClientPort();
        int[] ids = data.getIds();

        db.updateFilesForUser(ids, inetAddress, port);

        return new Response(RequestType.UPDATE, new UpdateResponseData(true));
    }

    public Response getSourcesResponse(SourcesRequestData data) {
        SourcesResponseData responseData = new SourcesResponseData();

        ServerDatabase db = DatabaseProvider.getServerDB();
        List<SourcesResponseData.Source> sources = db
                .listAllSeedsOf(data.getRequestedId())
                .stream()
                .map(userEntity -> new SourcesResponseData.Source(
                        userEntity.getPort(),
                        userEntity.getAddress()
                    )
                )
                .collect(Collectors.toList());

        responseData.addAll(sources);
        return new Response(RequestType.SOURCES, responseData);
    }

    public Response getListResponse(ListRequestData data) {
        ListResponseData responseData = new ListResponseData();

        ServerDatabase db = DatabaseProvider.getServerDB();
        List<ListResponseData.ListResponseItem> files = db
                .listAllFiles()
                .stream()
                .map(
                    it -> new ListResponseData.ListResponseItem(
                        it.getId(),
                        it.getName(),
                        it.getSize()
                    ))
                .collect(Collectors.toList());

        responseData.addAll(files);

        return new Response(RequestType.LIST, responseData);
    }
}
