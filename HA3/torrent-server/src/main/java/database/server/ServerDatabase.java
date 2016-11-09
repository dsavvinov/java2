package database.server;

import database.FileEntity;

import java.net.InetAddress;
import java.util.List;

public interface ServerDatabase {
    List<FileEntity> listAllFiles();

    List<UserEntity> listAllSeedsOf(int requestedId);

    UserEntity getUserEntity(String inetAddress, int port);

    FileEntity getFileEntity(int id);

    int uploadFile(String name, long size);

    void updateFilesForUser(int[] ids, String inetAddress, int port);

    LastID getLastID();

    void saveLastID(LastID lastID);
}
