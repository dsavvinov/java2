package database.client;

import database.FileEntity;

import java.io.File;
import java.util.List;

public interface ClientDatabase {
    List<FileEntity> listSeededFiles();

    void addFile(FileEntity file);

    List<FilePart> listFileParts(int id);

    void addAllPartsOfFile(FileEntity file);

    void addPartOfFile(FilePart part);

    FileEntity getFile(int id);

    FilePart getFilePart(int id, int part);
}
