package commands;

import gut.LocalRepository;
import exceptions.SerializationException;
import io.printing.Logger;

import java.io.IOException;


public class InitCommand extends AbstractCommand {
    @Override
    public void execute(Logger log) {
        try {
            LocalRepository.createNew();
            LocalRepository.saveToDisk();
        } catch (IOException | SerializationException e) {
            log.error("Error while creating repository: " + e.getMessage());
        }
        log.println("Gut: initialized empty repository successfully!");
    }
}
