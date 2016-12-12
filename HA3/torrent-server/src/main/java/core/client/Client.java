package core.client;

import core.p2p.Peer;
import database.DatabaseProvider;
import database.FileEntity;
import database.client.ClientDatabase;
import exceptions.InvalidProtocolException;
import io.Logger;
import io.StandardLogger;
import net.Message;
import net.protocols.ClientServerProtocol;
import net.protocols.Peer2PeerProtocol;
import net.queries.StatQuery;
import net.queries.requests.*;
import net.queries.responses.*;
import utils.Constants;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import static utils.Constants.*;

public class Client {
    public final short clientPort;
    public volatile boolean shutdown = false;    // for terminating update-thread

    private String rootDir;
    private Logger log = StandardLogger.getInstance();
    private Thread updaterThread = null;
    private Thread peerServerThread = null;
    private ClientServerProtocol clientServerProtocol = new ClientServerProtocol();
    private Peer2PeerProtocol p2pProtocol = new Peer2PeerProtocol();

    public Client(short clientPort, String rootDir) {
        this.clientPort = clientPort;
        this.rootDir = rootDir;
    }

    public Client(short clientPort, String rootDir, Logger log) {
        this.clientPort = clientPort;
        this.rootDir = rootDir;
        this.log = log;
    }

    public Message interact(Message request) {
        try (Socket serverSocket = new Socket(SERVER_ADDRESS, SERVER_PORT)) {
            log.trace("Sending request <" + request.toString() + ">");
            clientServerProtocol.writeRequest(request, serverSocket.getOutputStream());
            log.trace("Sent successfully, waiting for response...");

            Message response = clientServerProtocol.readResponse(request.getQuery(), serverSocket.getInputStream());
            log.trace("Got response <" + response.toString() + ">");

            log.trace("Closing connection");
            return response;
        } catch (UnknownHostException e) {
            log.error("Error: unknown host");
        } catch (IOException e) {
            log.error("IO Error: " + e.getMessage());
        } catch (InvalidProtocolException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public UploadResponse executeUploadCommand(String pathToFile) {
        Path curDir = Paths.get(rootDir);
        Path path = curDir.resolve(pathToFile);
        long size = path.toFile().length();
        String name = path.getFileName().toString();

        Message request = new UploadRequest(name, size);
        Message response = interact(request);
        if (response == null) {
            return null;
        }

        UploadResponse data = (UploadResponse) response;

        ClientDatabase db = DatabaseProvider.getClientDB();
        FileEntity file = new FileEntity(data.getId(), name, size);
        file.setLocalPath(pathToFile);
        db.addFile(file);
        db.addAllPartsOfFile(file);

        return data;
    }

    public void initClient() {
        updaterThread = new Thread(new Updater(this, log));
        peerServerThread = new Thread(new Peer(this, log, rootDir));
        updaterThread.start();
        peerServerThread.start();
    }

    @SuppressWarnings("Duplicates")
    public void shutdown() {
        shutdown = true;

        if (updaterThread != null) {
            // shutdown updater
            updaterThread.interrupt();
            while (updaterThread.isAlive()) {
                try {
                    updaterThread.join();
                } catch (InterruptedException ignored) {
                    // ignore spurious wake-ups
                }
            }
        }

        if (peerServerThread != null) {
            peerServerThread.interrupt();
            while (peerServerThread.isAlive()) {
                try {
                    peerServerThread.join();
                } catch (InterruptedException ignored) { }
            }
        }
    }

    public SourcesResponse executeSourcesCommand(String fileId) {
        int requestedId = Integer.parseInt(fileId);
        Message r = new SourcesRequest(requestedId);
        return (SourcesResponse) interact(r);
    }

    public ListResponse executeListCommand() {
        Message request = new ListRequest();
        ListResponse response = (ListResponse) interact(request);
        if (response == null) {
            return null;
        }

        ClientDatabase clientDB = DatabaseProvider.getClientDB();
        return response;
    }

    public UpdateResponse executeUpdateCommand() {
        ClientDatabase db = DatabaseProvider.getClientDB();
        List<FileEntity> seededFiles = db.listSeededFiles();

        int[] ids = seededFiles.stream().mapToInt(FileEntity::getId).toArray();

        Message request = new UpdateRequest(clientPort, ids);

        return (UpdateResponse) interact(request);
    }

    public StatResponse executeStatCommand(String address, String sport, String sid) {
        int id = Integer.parseInt(sid);
        int port = Integer.parseInt(sport);
        Message request = new StatRequest(id);

        try(Socket socket = new Socket(InetAddress.getByName(address), port)) {
            p2pProtocol.writeRequest(request, socket.getOutputStream());
            return (StatResponse) p2pProtocol.readResponse(new StatQuery(), socket.getInputStream());
        } catch (IOException | InvalidProtocolException e) {
            log.error(e.toString());
            return null;
        }
    }

    public void executeGetCommand(String address, String sport, String sid, String spart) {
        int id = Integer.parseInt(sid);
        int part = Integer.parseInt(spart);
        int port = Integer.parseInt(sport);
        InetAddress inetAddress = null;

        try {
            inetAddress = InetAddress.getByName(address);
        } catch (UnknownHostException e) {
            log.error(e.toString());
            return;
        }

        // We retrieve list of all files from the server to get file name
        ListResponse listResponse = executeListCommand();

        // Boring Disk IO
        ClientDatabase clientDatabase = DatabaseProvider.getClientDB();
        FileEntity fileEntity = clientDatabase.getFile(id);
        FileChannel fc;
        Path localPath;
        long offset = part * Constants.BLOCK_SIZE;
        try {
            Path cur = Paths.get(rootDir);
            localPath = cur.resolve(fileEntity.getName());

            if (Files.notExists(localPath)) {
                Files.createDirectories(localPath.getParent());
                Files.createFile(localPath);
                RandomAccessFile raFile = new RandomAccessFile(localPath.toFile(), "rw");
                raFile.setLength(fileEntity.getSize());
            }
            fc = FileChannel.open(localPath, StandardOpenOption.READ, StandardOpenOption.WRITE);
        } catch (IOException e) {
            log.error(e.toString());
            return;
        }

        // Actual downloading
        Message request = new GetRequest(id, part);

        log.trace("Connecting to " + inetAddress + "/" + port);
        try(SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress(inetAddress, port))) {
            p2pProtocol.writeRequest(request, socketChannel.socket().getOutputStream());
            long partSize = Math.min(fileEntity.getSize() - offset, BLOCK_SIZE);
            fc.transferFrom(socketChannel, offset, partSize);
        } catch (IOException e) {
            log.error("Error: can't open connection with peer " + e.getMessage());
            return;
        }

        // Adding to

    }
}
