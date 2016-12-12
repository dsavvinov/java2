package core.p2p;

import database.DatabaseProvider;
import database.FileEntity;
import database.client.ClientDatabase;
import database.client.FilePart;
import exceptions.InvalidProtocolException;
import io.Logger;
import net.Message;
import net.MessageHandler;
import net.protocols.Peer2PeerProtocol;
import net.queries.requests.GetRequest;
import net.queries.requests.StatRequest;
import net.queries.responses.StatResponse;
import utils.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class PeerHandler implements Runnable, MessageHandler<Void> {
    private final Logger log;
    private final SocketChannel peerSocket;
    private final String prefix;    // for logging
    private final Peer2PeerProtocol p2p = new Peer2PeerProtocol();
    private final String rootDir;

    public PeerHandler(SocketChannel peerSocket, Logger log, String rootDir) {
        this.peerSocket = peerSocket;
        this.log = log;
        prefix = "[" + peerSocket.socket().getInetAddress().toString() + "]";
        this.rootDir = rootDir;
    }

    @Override
    public void run() {
        InputStream inputStream;
        try {
            inputStream = peerSocket.socket().getInputStream();
        } catch (IOException e) {
            log.error("Error working with client socket: " + e.getMessage());
            return;
        }

        try {
            log.trace(prefix + " reading request");
            Message request = p2p.readRequest(inputStream);
            log.trace(prefix + " got request: <" + request.toString() + ">");

            request.dispatch(this);
        } catch (InvalidProtocolException e) {
            log.error("Protocol error while handling peer <" + peerSocket.toString()
                    + ">: " + e.getMessage());
        }
    }

    @Override
    public Void handle(GetRequest getRequest) {
        try {
            ClientDatabase clientDB = DatabaseProvider.getClientDB();
            FileEntity file = clientDB.getFile(getRequest.getId());
            FilePart part = clientDB.getFilePart(getRequest.getId(), getRequest.getPart());

            Path rel = Paths.get(file.getLocalPath());
            Path cur = Paths.get(rootDir);
            Path abs = cur.resolve(rel);
            FileChannel fc = FileChannel.open(abs);

            log.trace("Starting transfer with offset = " + part.getOffset() + ", size = " + part.getSize());
            long l = fc.transferTo(part.getOffset(), part.getSize(), peerSocket);
            log.trace("Finished transfer...");
        } catch (IOException e) {
            log.error("IO error while handling peer<" + peerSocket.toString() + ">: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Void handle(StatRequest statRequest) {
        ClientDatabase clientDB = DatabaseProvider.getClientDB();
        List<FilePart> fileParts = clientDB.listFileParts(statRequest.getId());
        int[] partsIds = fileParts
                .stream()
                .mapToInt(it -> (int) (it.getOffset() / Constants.BLOCK_SIZE) )
                .toArray();

        try {
            p2p.writeResponse(new StatResponse(partsIds), peerSocket.socket().getOutputStream());
        } catch (IOException e) {
            log.error("IO error while handling peer<" + peerSocket.toString() + ">: " + e.getMessage());
        }

        return null;
    }
}
