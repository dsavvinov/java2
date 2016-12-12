package core;

import database.FileEntity;
import database.client.ClientDatabase;
import database.client.FilePart;
import utils.Constants;

import java.util.*;

public class ClientDBMock implements ClientDatabase {
    ArrayList<FileEntity> seededFiles = new ArrayList<>();
    HashMap<Integer, ArrayList<FilePart>> ownedParts = new HashMap<>();

    @Override
    public List<FileEntity> listSeededFiles() {
        return seededFiles;
    }

    @Override
    public void addFile(FileEntity file) {
        seededFiles.add(file);
    }

    public void addFile(int dummyId) {
        seededFiles.add(allFiles.get(dummyId));
    }

    public static final FileEntity file1 = new FileEntity(0, "file1", 10);
    public static final FileEntity file2 = new FileEntity(1, "file2", 42);
    public static final FileEntity file3 = new FileEntity(2, "file3", 228);
    public static final FileEntity file4 = new FileEntity(3, "file4", 1337);

    // for convenience and testing purposes
    private static final ArrayList<FileEntity> allFiles =
            new ArrayList<>(Arrays.asList(file1, file2, file3, file4));

    @Override
    public List<FilePart> listFileParts(int id) {
        return ownedParts.get(id);
    }

    @Override
    public void addAllPartsOfFile(FileEntity file) {
        long curOffset = 0;
        ArrayList<FilePart> parts = new ArrayList<>();

        while (curOffset < file.getSize()) {
            parts.add(new FilePart(
                    file.getId(),
                    curOffset,
                    Math.min(file.getSize() - curOffset, Constants.BLOCK_SIZE
                    )
                )
            );
            curOffset += Constants.BLOCK_SIZE;
        }

        ownedParts.put(file.getId(), parts);
    }

    @Override
    public void addPartOfFile(FilePart part) {
        ArrayList<FilePart> fileParts = ownedParts.get(part.getFileID());
        fileParts.add(part);
        ownedParts.put(part.getFileID(), fileParts);
    }

    @Override
    public FileEntity getFile(int id) {
        for (int i = 0; i < seededFiles.size(); i++) {
            if (seededFiles.get(i).getId() == id) {
                return seededFiles.get(i);
            }
        }
        return null;
    }

    @Override
    public FilePart getFilePart(int id, int part) {
        ArrayList<FilePart> fileParts = ownedParts.get(id);
        for (FilePart filePart : fileParts) {
            if (filePart.getOffset() / Constants.BLOCK_SIZE == part) {
                return filePart;
            }
        }
        return null;
    }
}
