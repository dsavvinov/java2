package commands;

import exceptions.BranchNotFoundException;
import exceptions.CommitNotFoundException;
import gut.LocalRepository;
import exceptions.RepoNotFoundException;
import exceptions.SerializationException;
import io.printing.Logger;
import org.apache.commons.cli.*;

import java.io.IOException;

public class CheckoutCommand extends AbstractCommand {
    private static final Options options = new Options();

    @Override
    public AbstractCommand parseFrom(String[] args, Logger log) {
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine line = parser.parse(options, args);
            if (line.hasOption("b")) {
                shouldCheckBranch = true;
                argument = line.getOptionValue("b");
            } else if (line.hasOption("r")) {
                shouldCheckRevision = true;
                argument = line.getOptionValue("r");
            } else if (line.hasOption("h")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("checkout", options);
            }
        }
        catch (ParseException e) {
            log.error("Error: " + e.getMessage());
        }

        return this;
    }

    @Override
    public void execute(Logger log) {
        try {
            if (shouldCheckRevision) {
                checkRevision(argument);
            } else if (shouldCheckBranch) {
                checkBranch(argument);
            } else {
                return;
            }
        } catch ( SerializationException
                | RepoNotFoundException
                | CommitNotFoundException
                | BranchNotFoundException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error("Error working with file system: " + e.getMessage());
        }
        log.println("Checkout went successfully!");
    }

    private boolean shouldCheckBranch = false;
    private boolean shouldCheckRevision = false;
    private String argument = "";

    static {
        Option branch = Option.builder("b")
                .hasArg(true)
                .longOpt("branch")
                .argName("branch to checkout")
                .desc("Checkout an existing branch of a given name")
                .build();
        Option revision = Option.builder("r")
                .hasArg(true)
                .longOpt("revision")
                .argName("revision to checkout")
                .desc("Checkout a revision of a given code")
                .build();
        Option help = Option.builder("h")
                .hasArg(false)
                .longOpt("help")
                .desc("Show help")
                .build();

        OptionGroup whatToCheck = new OptionGroup();
        whatToCheck.addOption(branch);
        whatToCheck.addOption(revision);

        options.addOptionGroup(whatToCheck);
        options.addOption(help);
    }

    private void checkRevision(String revCode)
            throws RepoNotFoundException,
            SerializationException,
            IOException,
            CommitNotFoundException {
        LocalRepository.tryLoad();
        LocalRepository.checkoutCommit(revCode);
        LocalRepository.saveToDisk();
    }

    private void checkBranch(String branchName)
            throws  RepoNotFoundException,
                    SerializationException,
                    IOException,
            BranchNotFoundException {
        LocalRepository.tryLoad();
        LocalRepository.checkoutBranch(branchName);
        LocalRepository.saveToDisk();
    }
}
