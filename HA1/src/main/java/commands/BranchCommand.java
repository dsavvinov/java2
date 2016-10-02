package commands;

import exceptions.BranchAlreadyExistsException;
import exceptions.BranchNotFoundException;
import gut.LocalRepository;
import exceptions.RepoNotFoundException;
import exceptions.SerializationException;
import io.printing.Logger;
import org.apache.commons.cli.*;

import java.io.IOException;

public class BranchCommand extends AbstractCommand {
    @Override
    public AbstractCommand parseFrom(String[] args, Logger log) {
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine line = parser.parse(options, args);
            if (line.hasOption("d")) {
                shouldDelete = true;
                branchName = line.getOptionValue("d");
            } else if (line.hasOption("c")) {
                shouldCreate = true;
                branchName = line.getOptionValue("c");
            } else if (line.hasOption("h")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("branch", options);
            }
        } catch (ParseException e) {
            log.error("Error: " + e.getMessage());
        }

        return this;
    }

    @Override
    public void execute(Logger log) {
        try {
            if (shouldDelete) {
                deleteBranch(branchName);
            } else if (shouldCreate) {
                createBranch(branchName);
            } else {
                return;
            }
        } catch (RepoNotFoundException
                | SerializationException
                | BranchNotFoundException
                | BranchAlreadyExistsException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error("Error while working with file system: " + e.getMessage());
        }
        log.println("Success!");
    }

    private static final Options options = new Options();

    static {
        Option delete = Option.builder("d")
                .hasArg(true)
                .longOpt("delete")
                .argName("branch to delete")
                .desc("Delete an existing branch of a given name")
                .build();
        Option create = Option.builder("c")
                .hasArg(true)
                .longOpt("create")
                .argName("branch to create")
                .desc("Create new branch with a given name")
                .build();
        Option help = Option.builder("h")
                .hasArg(false)
                .longOpt("help")
                .desc("Show help")
                .build();

        OptionGroup delOrNew = new OptionGroup();
        delOrNew.addOption(delete);
        delOrNew.addOption(create);


        options.addOptionGroup(delOrNew);
        options.addOption(help);
    }

    private boolean shouldDelete = false;
    private boolean shouldCreate = false;
    private String branchName = "";

    private void deleteBranch(String branchName)
            throws RepoNotFoundException,
            SerializationException,
            IOException,
            BranchNotFoundException {
        LocalRepository.tryLoad();
        LocalRepository.deleteBranch(branchName);
        LocalRepository.saveToDisk();
    }

    private void createBranch(String branchName)
            throws RepoNotFoundException,
            SerializationException,
            IOException,
            BranchAlreadyExistsException {
        LocalRepository.tryLoad();
        LocalRepository.createBranch(branchName);
        LocalRepository.saveToDisk();
    }
}
