package commands;

import io.printing.Logger;
import org.apache.commons.cli.*;

public abstract class AbstractCommand {
    // default implementation for argument-less methods
    public AbstractCommand parseFrom(String[] args, Logger log) {
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine line = parser.parse(new Options(), args);
        }
        catch (ParseException e) {
            log.error("Error: " + e.getMessage());
        }

        return this;
    }

    public abstract void execute(Logger log);
}
