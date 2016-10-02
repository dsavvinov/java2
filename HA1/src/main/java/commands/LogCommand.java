package commands;

import gut.Branch;
import gut.Commit;
import gut.LocalRepository;
import gut.Tree;
import exceptions.RepoNotFoundException;
import exceptions.SerializationException;
import io.printing.Logger;

import java.util.List;

public class LogCommand extends AbstractCommand{
    @Override
    public void execute(Logger log) {
        try {
            LocalRepository.tryLoad();

            Tree tree = LocalRepository.getTree();
            Branch curBranch = tree.getCurrentBranch();
            List<Commit> history = tree.getHistory();

            log.println("Currently at branch <" + curBranch.getName() + ">");
            history.forEach( commit -> log.println(commit.toString()) );

            // No need to save repo as we haven't changed anything
        } catch (SerializationException | RepoNotFoundException e) {
            log.error(e.getMessage());
        }
    }
}
