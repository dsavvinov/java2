package core.client;

import database.DatabaseProvider;
import database.FileEntity;
import database.client.ClientDatabase;
import database.client.FilePart;
import exceptions.InvalidProtocolException;
import exceptions.WrongArgumentException;
import io.Logger;
import net.Peer2PeerProtocol;
import net.requests.GetRequestData;
import net.requests.Request;
import net.requests.RequestType;
import net.requests.StatRequestData;
import net.responses.GetResponseData;
import net.responses.Response;
import net.responses.StatResponseData;
import utils.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class PeerHandler implements Runnable {
    private final Logger log;
    private final SocketChannel peerSocket;
    private final String prefix;    // for logging

    public PeerHandler(SocketChannel peerSocket, Logger log) {
        this.peerSocket = peerSocket;
        this.log = log;
        prefix = "[" + peerSocket.socket().getInetAddress().toString() + "]";
    }

    @Override
    public void run() {
        InputStream inputStream;
        OutputStream outputStream;
        try {
            inputStream = peerSocket.socket().getInputStream();
            outputStream = peerSocket.socket().getOutputStream();
        } catch (IOException e) {
            log.error("Error working with client socket: " + e.getMessage());
            return;
        }

        try {
            log.trace(prefix + " reading request");
            Request request = Peer2PeerProtocol.readRequest(inputStream);
            log.trace(prefix + " got request: <" + request.toString() + ">");
            Response response = null;
            switch (request.getType()) {
                case STAT:
                    response = getStatResponse((StatRequestData) request.getData());
                    break;
                case GET:
                    response = getGetResponse((GetRequestData) request.getData());
                    break;
                default:
                    log.error("Wrong request type");
                    return;
            }

            if (request.getType() != RequestType.GET) {
                Peer2PeerProtocol.writeResponse(response, peerSocket.socket().getOutputStream());
                return;
            }

            uploadFile((GetResponseData) response.getData(), peerSocket);

        } catch (InvalidProtocolException e) {
            log.error("Protocol error while handling peer <" + peerSocket.toString()
                    + ">: " + e.getMessage());
        } catch (WrongArgumentException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error("IO Error: " + e.getMessage());
        }
    }

    private void uploadFile(GetResponseData data, SocketChannel peerSocket) throws IOException {
        log.trace("Starting transfer with offset = " + data.getOffset() + ", size = " + data.getSize());
        data.getFileChannel().transferTo(data.getOffset(), data.getSize(), peerSocket);
        log.trace("Finished transfer...");
    }

    private Response getGetResponse(GetRequestData data) throws IOException {
        ClientDatabase clientDB = DatabaseProvider.getClientDB();
        FileEntity file = clientDB.getFile(data.getId());
        FilePart part = clientDB.getFilePart(data.getId(), data.getPart());
        if (part == null) {
            return new Response(RequestType.GET, null);
        }

        Path rel = Paths.get(file.getLocalPath());
        Path cur = Paths.get(System.getProperty("user.dir"));
        Path abs = cur.resolve(rel);
        FileChannel fc = FileChannel.open(abs);
        return new Response(RequestType.GET, new GetResponseData(fc, part.getOffset(), part.getSize()));
    }

    private Response getStatResponse(StatRequestData data) {
        ClientDatabase clientDB = DatabaseProvider.getClientDB();
        List<FilePart> fileParts = clientDB.listFileParts(data.getId());
        int[] partsIds = fileParts
                .stream()
                .mapToInt(it -> (int) (it.getOffset() / Constants.BLOCK_SIZE) )
                .toArray();
        return new Response(RequestType.STAT, new StatResponseData(partsIds));
    }
}
