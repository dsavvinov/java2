package database.server;

import com.mongodb.MongoClient;
import database.FileEntity;
import io.Logger;
import io.StandardLogger;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class ServerDBMongoImpl implements ServerDatabase {
    private final Morphia morphia;
    private final Datastore datastore;
    private final Logger log = StandardLogger.getInstance();
    public ServerDBMongoImpl(String dbName) {
        morphia = new Morphia();
        morphia.mapPackage("core.database.client");

        datastore = morphia.createDatastore(new MongoClient("localhost"), dbName);
        datastore.ensureIndexes();
    }

    @Override
    public List<FileEntity> listAllFiles() {
        Query<FileEntity> query = datastore.createQuery(FileEntity.class);
        return query.asList();
    }

    @Override
    public List<UserEntity> listAllSeedsOf(int requestedId) {
        FileEntity requestedFile = getFileEntity(requestedId);
        if (requestedFile == null) {
            return Collections.emptyList();
        }

        List<Seed> seeds = requestedFile.getSeeds();

        // invalidate outdated seeds
        Iterator<Seed> iterator = seeds.iterator();
        while (iterator.hasNext()) {
            Seed cur = iterator.next();
            if (cur.isOutdated()) {
                iterator.remove();
            }
        }

        return seeds.stream().map(Seed::getUser).collect(Collectors.toList());
    }

    @Override
    public UserEntity getUserEntity(String inetAddress, int port) {
        String fullAddress = inetAddress + "/" + Integer.toString(port);
        Query<UserEntity> query = datastore.createQuery(UserEntity.class);
        List<UserEntity> users = query.field("fullAddress").equal(fullAddress).asList();

        if (users.size() > 1) {
            throw new InternalError("Error: count of users with address = " + fullAddress
                    + " is " + users.size() + " (expected 1, of course)");
        } else if (users.size() == 0) {
            return null;
        }

        return users.get(0);
    }

    @Override
    public FileEntity getFileEntity(int id) {
        Query<FileEntity> query = datastore.createQuery(FileEntity.class);
        List<FileEntity> files = query.field("id").equal(id).asList();

        if (files.size() > 1) {
            throw new InternalError("Error: count of files with id = " + id
                    + " is " + files.size() + " (expected 1, of course)");
        }
        if (files.size() == 0) {
            return null;
        }

        return files.get(0);
    }

    @Override
    public int uploadFile(String name, long size) {
        FileEntity newFile = new FileEntity(IDManager.get(), name, size);
        datastore.save(newFile);
        return newFile.getId();
    }

    @Override
    public void updateFilesForUser(int[] filesIDs, String inetAddress, int port) {
        UserEntity user = getUserEntity(inetAddress, port);
        if (user == null) {
            user = new UserEntity((short) port, inetAddress);
            datastore.save(user);
        }

        for (int id : filesIDs) {
            FileEntity fromDB = getFileEntity(id);
            if (fromDB == null) {
                log.error("Error: file with ID = " + id + " wasn't uploaded on server!");
                return;
            }
            boolean found = false;
            // try to update existing seed
            for (Seed seed : fromDB.getSeeds()) {
                if (seed.getUser() != null && seed.getUser().equals(user)) {
                    seed.update();
                    found = true;
                    break;
                }
            }

            // if not found, add as new seed
            if (!found) {
                fromDB.addSeed(user);
            }

            datastore.save(fromDB);
        }
    }

    @Override
    public LastID getLastID() {
        Query<LastID> query = datastore.createQuery(LastID.class);
        List<LastID> lastIDs = query.asList();

        if (lastIDs.size() == 0) {
            LastID initial = new LastID();
            initial.value = 0;
            datastore.save(initial);
            return initial;
        }
        if (lastIDs.size() > 1) {
            throw new InternalError("Count of LastID objects in DB is "
                    + lastIDs.size() + "(expected 1, of course)");
        }

        return lastIDs.get(0);
    }

    @Override
    public void saveLastID(LastID lastID) {
        datastore.save(lastID);
    }
}
