package core;

import database.FileEntity;
import database.server.LastID;
import database.server.ServerDatabase;
import database.server.UserEntity;

import java.util.*;
import java.util.stream.Collectors;

import static net.responses.SourcesResponseData.Source;

public class ServerDBMock implements ServerDatabase {
    public HashMap<Integer, HashSet<Source>> filesToSources = new HashMap<>();
    public HashMap<Integer, FileEntity> filesByID = new HashMap<>();

    public ServerDBMock() {
        filesByID.put(file1.getId(), file1);
        filesByID.put(file2.getId(), file2);
        filesByID.put(file3.getId(), file3);
        filesByID.put(file4.getId(), file4);

        filesToSources.put(0, new HashSet<>(Arrays.asList(user1)));
        filesToSources.put(1, new HashSet<>(Arrays.asList(user2)));
        filesToSources.put(2, new HashSet<>(Arrays.asList(user1, user2, user3)));
        filesToSources.put(3, new HashSet<>(Arrays.asList(user1, user3)));
    }


    @Override
    public List<FileEntity> listAllFiles() {
        return filesByID
                .entrySet()
                .stream()
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserEntity> listAllSeedsOf(int requestedId) {
        return filesToSources
                .get(requestedId)
                .stream()
                .map(source -> new UserEntity(source.getPort(), source.getHost()))
                .collect(Collectors.toList());
    }

    @Override
    public UserEntity getUserEntity(String inetAddress, int port) {
        switch(port) {
            case 1:
                return new UserEntity(user1.getPort(), user1.getHost());
            case 2:
                return new UserEntity(user2.getPort(), user2.getHost());
            case 3:
                return new UserEntity(user3.getPort(), user3.getHost());
            default:
                return null;
        }
    }

    @Override
    public FileEntity getFileEntity(int id) {
        return filesByID.get(id);
    }

    @Override
    public int uploadFile(String name, long size) {
        int newId = filesByID.size();
        filesByID.put(newId, new FileEntity(newId, name, size));
        return newId;
    }

    @Override
    public void updateFilesForUser(int[] ids, String inetAddress, int port) {
        Source seed = new Source((short) port, inetAddress);
        for (int id: ids) {
            HashSet<Source> sources = filesToSources.get(id);
            if (sources == null) {
                sources = new HashSet<>();
            }
            sources.add(seed);
            filesToSources.put(id, sources);
        }
    }

    @Override
    public LastID getLastID() {
        LastID lastID = new LastID();
        lastID.value = filesByID.size();
        return lastID;
    }

    @Override
    public void saveLastID(LastID lastID) {

    }

    /** Some constants */

    private static final FileEntity file1 = new FileEntity(0, "file1", 10);
    private static final FileEntity file2 = new FileEntity(1, "file2", 42);
    private static final FileEntity file3 = new FileEntity(2, "file3", 228);
    private static final FileEntity file4 = new FileEntity(3, "file4", 1337);

    private static final Source user1 = new Source((short) 1, "user_1");
    private static final Source user2 = new Source((short) 2, "user_2");
    private static final Source user3 = new Source((short) 3, "user_3");

}
