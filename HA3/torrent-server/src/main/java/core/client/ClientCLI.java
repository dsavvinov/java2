package core.client;

import exceptions.NotImplementedYet;
import io.Logger;
import io.StandardLogger;
import net.responses.*;

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

        Client client = new Client(clientPort);

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
                    ListResponseData list = client.executeListCommand();
                    log.info("List of files:");
                    log.info(String.join("\n", list
                            .stream()
                            .map(ListResponseData.ListResponseItem::toString)
                            .collect(Collectors.toList())
                    ));
                    break;
                case "sources":
                    SourcesResponseData sources = client.executeSourcesCommand(commandArgs[1]);
                    log.info("File sources are:");
                    log.info(String.join("\n", sources
                            .stream()
                            .map(SourcesResponseData.Source::toString)
                            .collect(Collectors.toList()))
                    );
                    break;
                case "upload":
                    UploadResponseData id = client.executeUploadCommand(commandArgs[1]);
                    log.info("Got id = " + id.getId());
                    break;
                case "update":
                    UpdateResponseData status = client.executeUpdateCommand();
                    break;
                case "stat":
                    StatResponseData stat = client.executeStatCommand(commandArgs[1], commandArgs[2], commandArgs[3]);
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
