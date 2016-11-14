package ru.spbau.mit.cli.commands;

import ru.spbau.mit.io.Logger;
import ru.spbau.mit.io.LoggerFactory;
import ru.spbau.mit.client.Client;
import ru.spbau.mit.server.Item;

import java.io.IOException;
import java.util.List;

public class ListCommand implements Command {
    static private Logger log = LoggerFactory.getDefaultLogger();
    private final String folder;

    public ListCommand(String[] args) {
        if (args.length > 2) {
            log.trace("ListCommand: Can't create ListCommand: too many arguments supplied");
            String argStr = String.join(", ", (CharSequence[]) args);
            throw new IllegalArgumentException("Too many arguments: " + argStr);
        }
        folder = args[1];
    }

    @Override
    public void run() throws IOException {
        Client client = null;
        try {
            log.trace("ListCommand: Connecting to server...");
            client = new Client();
            client.connect();
            log.trace("ListCommand: Connected");

            log.trace("ListCommand: Querying list of files..");
            List<Item> result = client.getList(folder);
            log.trace("ListCommand: List of files succesfully acquired, printing");
            result.forEach(it -> log.info(it.getPath() + (it.isDirectory() ? "/" : "")));
        } finally {
            if (client != null) {
                client.shutdown();
            }
        }
    }
}
