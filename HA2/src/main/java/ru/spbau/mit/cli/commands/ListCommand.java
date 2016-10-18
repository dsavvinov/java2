package ru.spbau.mit.cli.commands;

import ru.spbau.mit.io.Logger;
import ru.spbau.mit.io.LoggerFactory;
import ru.spbau.mit.client.Client;

import java.io.IOException;
import java.util.ArrayList;

public class ListCommand implements Command {
    static private Logger log = LoggerFactory.getDefaultLogger();

    public ListCommand(String[] args) {
        if (args.length > 1) {
            log.trace("ListCommand: Can't create ListCommand: too many arguments supplied");
            String argStr = String.join(", ", (CharSequence[]) args);
            throw new IllegalArgumentException("Too many arguments: " + argStr);
        }
    }

    @Override
    public void run() throws IOException {
        log.trace("ListCommand: Connecting to server...");
        Client client = new Client();
        client.connect();
        log.trace("ListCommand: Connected");

        log.trace("ListCommand: Querying list of files..");
        ArrayList<String> result = client.getList();
        log.trace("ListCommand: List of files succesfully acquired, printing");
        result.forEach(it -> log.info(it));
    }
}
