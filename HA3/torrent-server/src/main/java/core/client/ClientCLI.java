package core.client;

import core.p2p.PeerServer;
import core.p2p.PeerService;
import core.p2p.PeerServiceNIOBased;
import database.DatabaseProvider;
import exceptions.DownloadingException;
import exceptions.InvalidProtocolException;
import io.Logger;
import io.StandardLogger;
import net.protocols.ClientServerProtocol;
import net.protocols.Peer2PeerProtocol;
import net.queries.responses.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import static utils.Constants.CLIENT_DEFAULT_DB_NAME;

public class ClientCLI {
    private static Logger log = StandardLogger.getInstance();
    private static ServerService serverService;
    private static PeerService peerService;
    private static Updater updater;
    private static PeerServer peerServer;
    private static Downloader downloader;

    public static void main(String[] args) {
        if (args.length < 1) {
            log.error("Error: client port should be provided");
            return;
        }

        short clientPort;
        try {
            clientPort = Short.parseShort(args[0]);
        } catch (NumberFormatException e) {
            log.error("Error: can't parse client port");
            return;
        }

        String dbName = CLIENT_DEFAULT_DB_NAME;
        // Check if non-default db-name was specified
        if (args.length == 2) {
            dbName = args[1];
        }

        String rootDir = System.getProperty("user.dir");

        // Init variables
        serverService = new ServerServiceNIOBased(clientPort, new ClientServerProtocol(), DatabaseProvider.getClientDB(dbName), log);
        peerService = new PeerServiceNIOBased(rootDir, new Peer2PeerProtocol(), log);
        updater = new Updater(log, clientPort, new ClientServerProtocol(), DatabaseProvider.getClientDB(dbName));
        peerServer = new PeerServer(clientPort, rootDir, log, DatabaseProvider.getClientDB(dbName));
        downloader = new SequentialDownloader(log, DatabaseProvider.getClientDB(dbName), serverService, peerService, rootDir);

        // Start services
        log.trace("Starting updater-thread");
        updater.start();

        log.trace("Starting peer-server");
        try {
            peerServer.start();
        } catch (IOException e) {
            log.error("Error creating peer-server: " + e.getMessage());
        }

        Scanner console = new Scanner(System.in);
        System.out.println();
        log.trace("Initialization successful, starting REPL");
        while (true) {
            System.out.print("> ");
            String command = console.nextLine();
            String[] commandArgs = command.split(" ");  // TODO: escaped whitespaces?
            try {
                switch (commandArgs[0]) {
                    case "exit": {
                        updater.shutdown();
                        peerServer.shutdown();
                        return;
                    }
                    case "list": {
                        ListResponse list = serverService.list();
                        log.info("List of files:");
                        log.info(String.join("\n", list
                                .stream()
                                .map(ListResponse.ListResponseItem::toString)
                                .collect(Collectors.toList())
                        ));
                        break;
                    }
                    case "sources": {
                        SourcesResponse sources = serverService.sources(Integer.parseInt(commandArgs[1]));
                        log.info("File sources are:");
                        log.info(String.join("\n", sources
                                .stream()
                                .map(SourcesResponse.Source::toString)
                                .collect(Collectors.toList()))
                        );
                        break;
                    }
                    case "upload": {
                        Path relativePath = Paths.get(commandArgs[1]);
                        Path absPath = Paths.get(rootDir).resolve(relativePath);
                        if (Files.notExists(absPath)) {
                            log.error("Error: file not exists");
                            break;
                        }

                        UploadResponse response = serverService.upload(absPath);
                        log.info("Got id = " + response.getId());
                        break;
                    }
                    case "update": {
                        UpdateResponse status = serverService.update();
                        log.info("Got update status = " + status.getStatus());
                        break;
                    }
                    case "stat": {
                        StatResponse stat = peerService.stat(
                                /* peerAddress = */ commandArgs[1],
                                /* peerPort = */ Short.parseShort(commandArgs[2]),
                                /* fileID = */ Integer.parseInt(commandArgs[3])
                        );
                        log.info("Following parts are available:");
                        for (int i = 0; i < stat.getParts().length; i++) {
                            log.info(Integer.toString(stat.getParts()[i]));
                        }
                        break;
                    }
                    case "get": {
                        int fileID = Integer.parseInt(commandArgs[3]);
                        short peerPort = Short.parseShort(commandArgs[2]);
                        int partID = Integer.parseInt(commandArgs[4]);

                        // Make list-query to get fileName and fileSize
                        ListResponse list = serverService.list();
                        Optional<ListResponse.ListResponseItem> fileItem =
                                list.stream().filter(it -> it.id != fileID).findAny();
                        if (!fileItem.isPresent()) {
                            log.error("Error: unknown file with id = " + fileID);
                            return;
                        }

                        String downloadPath = Paths.get(rootDir).resolve(fileItem.get().name).toString();
                        long fileSize = fileItem.get().size;

                        peerService.get( /* peerAddress = */ commandArgs[1], peerPort, fileID,
                                partID, downloadPath, fileSize
                        );
                        break;
                    }
                    case "download": {
                        int fileID = Integer.parseInt(commandArgs[1]);
                        downloader.downloadFile(fileID);
                        break;
                    }
                    default:
                        log.error("Unknown command");
                }
            } catch (InvalidProtocolException | DownloadingException | IOException e) {
                log.error("Error executing command: " + e.getMessage());
            }
        }
    }
}
