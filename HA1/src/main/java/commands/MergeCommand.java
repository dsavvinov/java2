package commands;

import exceptions.BranchNotFoundException;
import gut.LocalRepository;
import exceptions.RepoNotFoundException;
import exceptions.SerializationException;
import io.printing.Logger;
import org.apache.commons.cli.*;

import java.io.IOException;

public class MergeCommand extends AbstractCommand {
    @Override
    public AbstractCommand parseFrom(String[] args, Logger log) {
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine line = parser.parse(options, args);
            message = line.getOptionValue("m");
            branchName = line.getOptionValue("b");
        }
        catch (ParseException e) {
            log.error("Error: " + e.getMessage());
        }

        return this;
    }

    @Override
    public void execute(Logger log) {
        if (message == null || branchName == null) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Merge", options);
            return;
        }

        try {
            LocalRepository.tryLoad();
            LocalRepository.mergeWith(branchName, message, log);
            LocalRepository.saveToDisk();
        } catch (RepoNotFoundException | BranchNotFoundException | SerializationException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error("Error while working with file system: " + e.getMessage());
        }
    }

    private static final Options options = new Options();

    private String message = null;
    private String branchName = null;

    static {
        Option branch = Option.builder("b")
                .hasArg(true)
                .longOpt("branch")
                .argName("branch to merge")
                .build();
        Option message = Option.builder("m")
                .hasArg(true)
                .longOpt("message")
                .argName("merge-commit message")
                .build();
        Option help = Option.builder("h")
                .hasArg(false)
                .longOpt("help")
                .desc("Show help")
                .build();

        options.addOption(message);
        options.addOption(branch);
        options.addOption(help);
    }
}
