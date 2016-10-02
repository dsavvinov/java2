package commands;


import gut.LocalRepository;
import exceptions.RepoNotFoundException;
import exceptions.SerializationException;
import io.printing.Logger;

import java.io.IOException;

public class CleanCommand extends AbstractCommand {
    @Override
    public void execute(Logger log) {
        try {
            LocalRepository.tryLoad();
            LocalRepository.clean();
            LocalRepository.saveToDisk();
            log.println("Cleaned succesfully!");
        } catch (RepoNotFoundException | SerializationException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error("Error working with file system: " + e.getMessage());
        }
    }
}
