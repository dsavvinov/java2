package ru.spbau.mit.cli.commands;

import ru.spbau.mit.io.Logger;
import ru.spbau.mit.io.LoggerFactory;
import ru.spbau.mit.client.Client;

import java.io.IOException;

public class GetCommand implements Command {
    private static final Logger log = LoggerFactory.getDefaultLogger();
    private final String fileName;

    public GetCommand(String[] args) {
        if (args.length < 2) {
            log.trace("GetCommand: Can't create GetCommand, no file specified");
            throw new IllegalArgumentException("No file specified");
        }
        if (args.length > 3) {
            log.trace("GetCommand: Can't create GetCommand, too many arguments");
            String argStr = String.join(", ", (CharSequence []) args);
            throw new IllegalArgumentException("Too many arguments: " + argStr);
        }
        fileName = args[1];
    }

    @Override
    public void run() throws IOException {
        Client client = null;
        try {
            log.trace("GetCommand: Connecting to server...");

            client = new Client();
            client.connect();
            log.trace("GetCommand: Connected");

            log.trace("GetCommand: Downloading file...");
            client.getFile(fileName);
            log.trace("GetCommand: File downloaded successfully!");

            log.trace("GetCommand: Shutting down client...");
            client.shutdown();
            log.trace("GetCommand: Client shut down gracefully");
        } finally {
            if (client != null) {
                client.shutdown();
            }
        }
    }
}
