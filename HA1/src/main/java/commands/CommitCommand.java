package commands;

import gut.LocalRepository;
import exceptions.RepoNotFoundException;
import exceptions.SerializationException;
import io.printing.Logger;
import org.apache.commons.cli.*;

import java.io.IOException;

public class CommitCommand extends AbstractCommand {
    @Override
    public AbstractCommand parseFrom(String[] args, Logger log) {
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine line = parser.parse(options, args);
            if (line.hasOption("m")) {
                message = line.getOptionValue("m");
            } else {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("Commit", options);
            }
        }
        catch (ParseException e) {
            log.error("Error: " + e.getMessage());
        }

        return this;
    }

    public void execute(Logger log) {
        if (message == null) {
            return;
        }

        try {
            LocalRepository.tryLoad();
            LocalRepository.commitStage(message);
            LocalRepository.saveToDisk();
            log.println("Committed successfully!");
        } catch (RepoNotFoundException | SerializationException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error("Error while working with file system: " + e.getMessage());
        }
    }

    private static final Options options = new Options();

    static {
        Option message = Option.builder("m")
                .hasArg(true)
                .longOpt("message")
                .argName("commit message")
                .build();
        Option help = Option.builder("h")
                .hasArg(false)
                .longOpt("help")
                .desc("Show help")
                .build();

        options.addOption(message);
        options.addOption(help);
    }

    private String message = null;
}
