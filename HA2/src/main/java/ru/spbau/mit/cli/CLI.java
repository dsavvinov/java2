package ru.spbau.mit.cli;


import ru.spbau.mit.cli.commands.Command;
import ru.spbau.mit.cli.commands.GetCommand;
import ru.spbau.mit.cli.commands.ListCommand;
import ru.spbau.mit.cli.commands.StartServerCommand;
import ru.spbau.mit.io.Logger;
import ru.spbau.mit.io.LoggerFactory;

import java.io.IOException;

public class CLI {
    private static Logger log = LoggerFactory.getDefaultLogger();

    public static void main(String[] args) {
        if (args.length == 0) {
            log.error("No arguments were supplied. Use \"help\" to get list of possible commands");
            return;
        }

        Command curCmd;
        try {
            switch (args[0]) {
                case "list":
                    log.trace("Main: Creating ListCommand");
                    curCmd = new ListCommand(args);
                    break;
                case "get":
                    log.trace("Main: creating GetCommand");
                    curCmd = new GetCommand(args);
                    break;
                case "server":
                    log.trace("Main: creating StartServerCommand");
                    curCmd = new StartServerCommand(args);
                    break;
                default:
                    log.error("Unrecognized command: " + args[0]);
                    return;
            }
            curCmd.run();
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error("IO Error " + e.getMessage());
        }
    }
}
