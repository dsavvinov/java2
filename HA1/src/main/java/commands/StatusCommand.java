package commands;

import gut.Difference;
import gut.LocalRepository;
import gut.LocalStageArea;
import io.printing.Logger;
import io.printing.PrettyPrinter;
import exceptions.RepoNotFoundException;
import exceptions.SerializationException;

import java.io.IOException;

public class StatusCommand extends AbstractCommand{
    @Override
    public void execute(Logger log) {
        try {
            LocalRepository.tryLoad();
            LocalStageArea lsa = LocalRepository.getLsa();
            Difference stageToHead = LocalRepository.getDiffToHead(lsa.getState());
            Difference indexToHead = LocalRepository.getDiffIndexToHead();
            Difference notStagedChanges = indexToHead.subtract(stageToHead);

            PrettyPrinter.prettyPrintStagedChanges(stageToHead, log);
            log.println("");
            PrettyPrinter.prettyPrintNotStagedChanges(notStagedChanges, log);

            // No need to save repo as we haven't changed anything
        } catch (RepoNotFoundException | SerializationException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
           log.error("Error working with file system: " + e.getMessage());
        }
    }
}
