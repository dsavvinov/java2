package ru.spbau.mit.cli.commands;


import ru.spbau.mit.server.Server;

import java.io.IOException;

public class StartServerCommand implements Command {
    public StartServerCommand(String[] args) {
        if (args.length > 1) {
            throw new IllegalArgumentException("Too much arguments supplied");
        }
    }

    @Override
    public void run() throws IOException {
        Server server = new Server();
        server.start();
    }
}
