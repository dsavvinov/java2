package commands;

import exceptions.FileNotVersionedException;
import gut.LocalRepository;
import exceptions.RepoNotFoundException;
import exceptions.SerializationException;
import io.printing.Logger;
import org.apache.commons.cli.*;

import java.io.IOException;

public class ResetCommand extends AbstractCommand{
    @Override
    public AbstractCommand parseFrom(String[] args, Logger log) {
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine line = parser.parse(options, args);
            if (line.hasOption("f")) {
                relPath = line.getOptionValue("f");
            } else {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("Reset", options);
            }
        }
        catch (ParseException e) {
            log.error("Error: " + e.getMessage());
        }

        return this;
    }

    @Override
    public void execute(Logger log) {
        if (relPath == null) {
            return;
        }

        try {
            LocalRepository.tryLoad();
            LocalRepository.reset(relPath);
            LocalRepository.saveToDisk();
            log.println("Reseted successfully!");
        } catch (RepoNotFoundException | FileNotVersionedException | SerializationException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error("Error while working with file system: " + e.getMessage());
        }
    }

    private static final Options options = new Options();

    private String relPath;

    static {
        Option file = Option.builder("f")
                .hasArg(true)
                .longOpt("file")
                .argName("FileObject to reset")
                .desc("Remove file from the stage area")
                .build();
        Option help = Option.builder("h")
                .hasArg(false)
                .longOpt("help")
                .desc("Show help")
                .build();

        options.addOption(file);
        options.addOption(help);
    }
}
