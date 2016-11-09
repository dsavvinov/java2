package database;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import database.server.LastID;
import database.server.Seed;
import database.server.ServerDatabase;
import database.server.UserEntity;
import net.responses.SourcesResponseData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class ServerDBTests {
    private ServerDatabase serverDB = DatabaseProvider.getServerDB("tests-db");

    @Before
    public void prepare() {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase db = mongoClient.getDatabase("tests-db");
        db.drop();
    }

    @After
    public void teardown() {
        // clean-up
        MongoClient mongoClient = new MongoClient();
        MongoDatabase db = mongoClient.getDatabase("tests-db");
        db.drop();
    }

    static {
        // Turn off noisy mongo logging
        java.util.logging.Logger mongoLogger = java.util.logging.Logger.getLogger("org.mongodb.driver");
        java.util.logging.Logger morphiaLogger = java.util.logging.Logger.getLogger("org.mongodb.morphia");

        morphiaLogger.setLevel(Level.SEVERE);
        mongoLogger.setLevel(Level.SEVERE);
    }

    @Test
    public void EmptyDBTest() {
        assertEquals(0, serverDB.getLastID().value);

        List<FileEntity> lst = serverDB.listAllFiles();
        assertEquals(0, lst.size());
        assertEquals(0, serverDB.listAllSeedsOf(312381).size());
    }

    /** here and below "static" used in the sense of static
     * (constant) content of DB, as opposed to "dynamic",
     * when DB contents changes during the test.
     */
    @Test
    public void StaticLastIDTest() {
        fillDB();

        LastID lastID = serverDB.getLastID();
        assertEquals(4, lastID.value);
    }

    @Test
    public void StaticGetFileTest() {
        fillDB();

        FileEntity fileEntity = serverDB.getFileEntity(2);
        assertEquals("file3", fileEntity.getName());
        assertEquals(228, fileEntity.getSize());
        Set<UserEntity> actualSeeds = fileEntity.getSeeds().stream().map(Seed::getUser).collect(Collectors.toSet());
        HashSet<UserEntity> expectedSeeds = new HashSet<>(Arrays.asList(user2, user3, user1));
        assertEquals(expectedSeeds, actualSeeds);
    }

    @Test
    public void StaticGetUserTest() {
        fillDB();

        UserEntity user_1 = serverDB.getUserEntity("user_1", 1);
        assertEquals(user1, user_1);
    }

    @Test
    public void StaticGetAllFilesTest() {
        fillDB();

        HashSet<FileEntity> actual = new HashSet<>(serverDB.listAllFiles());
        HashSet<FileEntity> expected = new HashSet<>(Arrays.asList(file1, file2, file3, file4));

        assertEquals(expected, actual);
    }

    @Test
    public void StaticGetAllSeedsTest() {
        fillDB();

        HashSet<UserEntity> actual = new HashSet<>(serverDB.listAllSeedsOf(2));
        HashSet<UserEntity> expected = new HashSet<>(Arrays.asList(user1, user2, user3));

        assertEquals(expected, actual);
    }

    @Test
    public void UpdateTest() {
        fillDB();

        serverDB.updateFilesForUser(new int[]{0, 1, 2, 3}, "user_1", 1);

        HashSet<UserEntity> actual = new HashSet<>(serverDB.listAllSeedsOf(1));
        HashSet<UserEntity> expected = new HashSet<>(Arrays.asList(user1, user2));

        assertEquals(expected, actual);
    }

    @Test
    public void UploadTest() {
        fillDB();

        int id = serverDB.uploadFile("file5", 322);
        assertEquals(4, id);

        long cnt = serverDB.listAllFiles().stream().filter(it -> it.getId() == 4).count();
        assertEquals(1, cnt);

        // user isn't updated info yet!
        assertEquals(0, serverDB.listAllSeedsOf(4).size());

        // now update
        serverDB.updateFilesForUser(new int[]{1, 2, 4}, "user2", 2);

        // and check that seed appeared
        assertEquals(1, serverDB.listAllSeedsOf(4).size());
    }

    @Test
    public void NewUserTest() {
        fillDB();

        serverDB.updateFilesForUser(new int[]{0, 1, 2, 3}, "user14", 14);

        // Check that new seed appeared
        assertEquals(2, serverDB.listAllSeedsOf(0).size());
        assertEquals(2, serverDB.listAllSeedsOf(1).size());
        assertEquals(4, serverDB.listAllSeedsOf(2).size());
        assertEquals(3, serverDB.listAllSeedsOf(3).size());

        // Check that new user appeared
        UserEntity user14 = serverDB.getUserEntity("user14", 14);
        assertEquals(new UserEntity((short) 14, "user14"), user14);
    }

    private void fillDB() {
        Morphia morphia = new Morphia();
        morphia.mapPackage("core.database.client");

        Datastore datastore = morphia.createDatastore(new MongoClient("localhost"), "tests-db");
        datastore.ensureIndexes();

        lastID.value = 4;

        file1.addSeed(user1);
        file3.addSeed(user1);
        file4.addSeed(user1);

        file2.addSeed(user2);
        file3.addSeed(user2);

        file3.addSeed(user3);
        file4.addSeed(user3);

        datastore.save(user1, user2, user3);
        datastore.save(file1, file2, file3, file4);
        datastore.save(lastID);
    }

    /** Some constants */

    private static final FileEntity file1 = new FileEntity(0, "file1", 10);
    private static final FileEntity file2 = new FileEntity(1, "file2", 42);
    private static final FileEntity file3 = new FileEntity(2, "file3", 228);
    private static final FileEntity file4 = new FileEntity(3, "file4", 1337);

    private static final UserEntity user1 = new UserEntity((short) 1, "user_1");
    private static final UserEntity user2 = new UserEntity((short) 2, "user_2");
    private static final UserEntity user3 = new UserEntity((short) 3, "user_3");

    private static final LastID lastID = new LastID();
}
