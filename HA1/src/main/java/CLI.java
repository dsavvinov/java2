import commands.*;
import io.printing.Logger;
import io.printing.StandardOutputLogger;

public class CLI {
    private static String errorString =
            "Error: can't parse command from the input\n" +
                    "Type \"gut help\" for help";

    private static String helpString =
            "Gut: Lightweight and fast VCS\n" +
                    "Usage:\n" +
                    "   help     - show this help\n" +
                    "   add      - add file to the index\n" +
                    "   branch   - work with branches\n" +
                    "   checkout - switch to another branch or revision\n" +
                    "   clean    - remove all not staged files from the working tree\n" +
                    "   commit   - commit changes\n" +
                    "   init     - create new repo in current folder.\n" +
                    "              WARNING: old .gut-folder will be deleted\n" +
                    "   log      - show log of changes in this branch\n" +
                    "   merge    - merge changes\n" +
                    "   remove   - remove file from the working tree and stage removal\n" +
                    "   reset    - cancel staged changes\n" +
                    "   status   - show status of index and working tree\n" +
                    "\n" +
                    "All commands support \"-h\" option (e.g. gut commit -h),\n" +
                    "that shows help specific to this command\n";


    public static void main(String[] args) {
        Logger log = new StandardOutputLogger();
        if (args.length == 0) {
            log.println(errorString);
            return;
        }
        AbstractCommand parsedCmd = null;
        /* In fact, all commands can't be treated as an arguments to Gut
            because each one have it's own set of arguments.
            So, we have to parse command name manually, and only after that
            handle control to the corresponding parser of command line arguments.
         */
        switch (args[0]) {
            case "add":
                parsedCmd = new AddCommand();
                break;
            case "branch":
                parsedCmd = new BranchCommand();
                break;
            case "checkout":
                parsedCmd = new CheckoutCommand();
                break;
            case "clean":
                parsedCmd = new CleanCommand();
                break;
            case "commit":
                parsedCmd = new CommitCommand();
                break;
            case "init":
                parsedCmd = new InitCommand();
                break;
            case "log":
                parsedCmd = new LogCommand();
                break;
            case "merge":
                parsedCmd = new MergeCommand();
                break;
            case "rm":
                parsedCmd = new RemoveCommand();
                break;
            case "reset":
                parsedCmd = new ResetCommand();
                break;
            case "status":
                parsedCmd = new StatusCommand();
                break;
            case "help":
                log.println(helpString);
                break;
            default:
                log.println(errorString);
                break;
        }
        if (parsedCmd != null) {
            parsedCmd.parseFrom(args, log).execute(log);
        }
    }
}
