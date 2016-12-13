package core.p2p;

import exceptions.InvalidProtocolException;
import io.Logger;
import net.Message;
import net.Protocol;
import net.queries.StatQuery;
import net.queries.requests.GetRequest;
import net.queries.requests.StatRequest;
import net.queries.responses.StatResponse;
import utils.Constants;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static utils.Constants.BLOCK_SIZE;

public class PeerServiceNIOBased implements PeerService {
    private final Logger log;
    private final Protocol protocol;

    public PeerServiceNIOBased(String rootDir, Protocol protocol, Logger log) {
        this.log = log;
        this.protocol = protocol;
    }

    @Override
    public void get(String peerAddress, short peerPort, int fileID, int partID, String path, long fileSize)
            throws IOException {
        InetAddress inetAddress = InetAddress.getByName(peerAddress);
        Path downloadPath = Paths.get(path);
        createFileIfNecessary(downloadPath, fileSize);
        log.trace("Opening file <" + downloadPath.toString() + ">");
        try(FileChannel fc = FileChannel.open(downloadPath, StandardOpenOption.READ, StandardOpenOption.WRITE)) {
            log.trace("Connecting to " + inetAddress + "/" + peerPort);
            try (SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress(inetAddress, peerPort))) {
                Message request = new GetRequest(fileID, partID);
                protocol.writeRequest(request, socketChannel.socket().getOutputStream());

                long offset = partID * Constants.BLOCK_SIZE;
                // Last block may have size less than BLOCK_SIZE, so we have to
                // carefully demand exact size (otherwise, tranferFrom() call will
                // block forever, waiting for the whole BLOCK_SIZE amount of data)
                long partSize = Math.min(fileSize - offset, BLOCK_SIZE);

                fc.transferFrom(socketChannel, offset, partSize);
            }
        }


    }

    @Override
    public StatResponse stat(String peerAddress, short peerPort, int fileID)
            throws IOException, InvalidProtocolException {
        log.trace("Sending stat request to the peer <" + peerAddress + ":" + peerPort + ">");
        Message request = new StatRequest(fileID);

        try(Socket socket = new Socket(InetAddress.getByName(peerAddress), peerPort)) {
            protocol.writeRequest(request, socket.getOutputStream());
            StatResponse statResponse = (StatResponse) protocol.readResponse(new StatQuery(), socket.getInputStream());
            log.trace("Got response <" + statResponse.toString() +">") ;
            return statResponse;
        }
    }

    private void createFileIfNecessary(Path path, long size) throws IOException {
        if (Files.notExists(path)) {
            Files.createFile(path);
            RandomAccessFile raFile = new RandomAccessFile(path.toFile(), "rw");
            raFile.setLength(size);
        }
    }
}
