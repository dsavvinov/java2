package core.client;

import io.Logger;
import io.StandardLogger;
import net.queries.responses.*;

import java.util.Scanner;
import java.util.stream.Collectors;

public class ClientCLI {
    private static final Logger log = StandardLogger.getInstance();

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

        Client client = new Client(clientPort, System.getProperty("user.dir"));

        log.trace("Connecting to server");
        client.initClient();
        log.trace("Connected successfully, starting REPL");
        Scanner console = new Scanner(System.in);
        System.out.println();
        while (true) {
            System.out.print("> ");
            String command = console.nextLine();
            String[] commandArgs = command.split(" ");  // TODO: escaped whitespaces?
            switch (commandArgs[0]) {
                case "exit":
                    client.shutdown();
                    return;
                case "list":
                    ListResponse list = client.executeListCommand();
                    log.info("List of files:");
                    log.info(String.join("\n", list
                            .stream()
                            .map(ListResponse.ListResponseItem::toString)
                            .collect(Collectors.toList())
                    ));
                    break;
                case "sources":
                    SourcesResponse sources = client.executeSourcesCommand(commandArgs[1]);
                    log.info("File sources are:");
                    log.info(String.join("\n", sources
                            .stream()
                            .map(SourcesResponse.Source::toString)
                            .collect(Collectors.toList()))
                    );
                    break;
                case "upload":
                    UploadResponse id = client.executeUploadCommand(commandArgs[1]);
                    log.info("Got id = " + id.getId());
                    break;
                case "update":
                    UpdateResponse status = client.executeUpdateCommand();
                    break;
                case "stat":
                    StatResponse stat = client.executeStatCommand(commandArgs[1], commandArgs[2], commandArgs[3]);
                    log.info("Following parts are available:");
                    for (int i = 0; i < stat.getParts().length; i++) {
                        log.info(Integer.toString(stat.getParts()[i]));
                    }
                    break;
                case "get":
                    client.executeGetCommand(commandArgs[1], commandArgs[2], commandArgs[3], commandArgs[4]);
                    break;
                default:
                    log.error("Unknown commands");
            }
        }
    }
}
