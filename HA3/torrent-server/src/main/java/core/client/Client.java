package core.client;

import database.DatabaseProvider;
import database.FileEntity;
import database.client.ClientDatabase;
import exceptions.InvalidProtocolException;
import exceptions.WrongArgumentException;
import io.Logger;
import io.StandardLogger;
import net.ClientServerProtocol;
import net.Peer2PeerProtocol;
import net.requests.*;
import net.responses.*;
import utils.Constants;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.*;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import static utils.Constants.BLOCK_SIZE;
import static utils.Constants.SERVER_ADDRESS;
import static utils.Constants.SERVER_PORT;

public class Client {
    public final short clientPort;
    private Logger log = StandardLogger.getInstance();
    private Thread updaterThread = null;
    private Thread peerServerThread = null;

    volatile boolean shutdown = false;    // for terminating update-thread

    public Client(short clientPort) {
        this.clientPort = clientPort;
    }

    public Client(short clientPort, Logger log) {
        this.clientPort = clientPort;
        this.log = log;
    }

    public Response interact(Request request) {
        try (Socket serverSocket = new Socket(SERVER_ADDRESS, SERVER_PORT)) {
            log.trace("Sending request <" + request.toString() + ">");
            ClientServerProtocol.writeRequest(request, serverSocket.getOutputStream());
            log.trace("Sent successfully, waiting for response...");
            Response response = ClientServerProtocol.readResponse(request.getType(), serverSocket.getInputStream());
            log.trace("Got response <" + response.toString() + ">");
            log.trace("Closing connection");
            return response;
        } catch (UnknownHostException e) {
            log.error("Error: unknown host");
        } catch (IOException e) {
            log.error("IO Error: " + e.getMessage());
        } catch (WrongArgumentException | InvalidProtocolException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public UploadResponseData executeUploadCommand(String pathToFile) {
        Path curDir = Paths.get(System.getProperty("user.dir"));
        Path path = curDir.resolve(pathToFile);
        long size = path.toFile().length();
        String name = path.getFileName().toString();

        Request request = new Request(RequestType.UPLOAD, new UploadRequestData(name, size));
        Response response = interact(request);
        if (response == null) {
            return null;
        }

        UploadResponseData data = (UploadResponseData) response.getData();

        ClientDatabase db = DatabaseProvider.getClientDB();
        FileEntity file = new FileEntity(data.getId(), name, size);
        file.setLocalPath(pathToFile);
        db.addFile(file);
        db.addAllPartsOfFile(file);

        return data;
    }

    public SourcesResponseData executeSourcesCommand(String fileId) {
        int requestedId = Integer.parseInt(fileId);
        Request r = new Request(RequestType.SOURCES, new SourcesRequestData(requestedId));
        Response response = interact(r);
        if (response == null) {
            return null;
        }

        return (SourcesResponseData) response.getData();
    }

    public ListResponseData executeListCommand() {
        Request request = new Request(RequestType.LIST, new ListRequestData());
        Response response = interact(request);
        if (response == null) {
            return null;
        }

        ClientDatabase clientDB = DatabaseProvider.getClientDB();
        ((ListResponseData) response.getData()).forEach(it -> {
            FileEntity file = new FileEntity(it.id, it.name, it.size);
            file.setLocalPath(it.name);
            clientDB.addFile(file);
        });

        return (ListResponseData) response.getData();
    }

    public void initClient() {
        updaterThread = new Thread(new Updater(this));
        peerServerThread = new Thread(new Peer(this));
        updaterThread.start();
        peerServerThread.start();
    }

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
            // shutdown peer server
        }
    }

    public UpdateResponseData executeUpdateCommand() {
        ClientDatabase db = DatabaseProvider.getClientDB();
        List<FileEntity> seededFiles = db.listSeededFiles();

        int[] ids = seededFiles.stream().mapToInt(FileEntity::getId).toArray();

        Request request = new Request(
                RequestType.UPDATE, new UpdateRequestData(clientPort, ids)
        );

        Response response = interact(request);
        return (UpdateResponseData) response.getData();
    }

    public StatResponseData executeStatCommand(String address, String sport, String sid) {
        int id = Integer.parseInt(sid);
        int port = Integer.parseInt(sport);
        Request request = new Request(RequestType.STAT, new StatRequestData(id));

        try(Socket socket = new Socket(InetAddress.getByName(address), port)) {
            Peer2PeerProtocol.writeRequest(request, socket.getOutputStream());
            Response response = Peer2PeerProtocol.readResponse(request.getType(), socket.getInputStream());
            return (StatResponseData) response.getData();
        } catch (IOException | WrongArgumentException | InvalidProtocolException e) {
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

        // Boring Disk IO
        ClientDatabase clientDatabase = DatabaseProvider.getClientDB();
        FileEntity fileEntity = clientDatabase.getFile(id);
        FileChannel fc;
        long offset = part * Constants.BLOCK_SIZE;
        try {
            Path relPath = Paths.get(fileEntity.getLocalPath());
            Path cur = Paths.get(System.getProperty("user.dir"));
            Path path = cur.resolve(relPath);
            log.info(path.toString());
            if (Files.notExists(path)) {
                Files.createFile(path);
                RandomAccessFile raFile = new RandomAccessFile(path.toFile(), "rw");
                raFile.setLength(fileEntity.getSize());
            }
            fc = FileChannel.open(path, StandardOpenOption.READ, StandardOpenOption.WRITE);
        } catch (IOException e) {
            log.error(e.toString());
            return;
        }

        Request request = new Request(RequestType.GET, new GetRequestData(id, part));

        log.trace("Connecting to " + inetAddress + "/" + port);
        try(SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress(inetAddress, port))) {
            Peer2PeerProtocol.writeRequest(request, socketChannel.socket().getOutputStream());
            long partSize = Math.min(fileEntity.getSize() - offset, BLOCK_SIZE);
            fc.transferFrom(socketChannel, offset, BLOCK_SIZE);
        } catch (IOException e) {
            log.error("Error: can't open connection with peer " + e.getMessage());
        } catch (WrongArgumentException e) {
            e.printStackTrace();
        }
    }
}
