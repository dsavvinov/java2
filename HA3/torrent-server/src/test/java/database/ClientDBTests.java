package database;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import database.client.ClientDBMongoImpl;
import database.client.ClientDatabase;
import database.client.FilePart;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utils.Constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;

import static org.junit.Assert.assertEquals;

public class ClientDBTests {
    private ClientDatabase clientDB = DatabaseProvider.getClientDB("tests-db");
    private static final FileEntity file1 = new FileEntity(0, "file1", 10);
    private static final FileEntity file2 = new FileEntity(1, "file2", 42);
    private static final FileEntity file3 = new FileEntity(2, "file3", 228);
    private static final FileEntity file4 = new FileEntity(3, "file4", 1337);

    static {
        // Turn off noisy mongo logging
        java.util.logging.Logger mongoLogger = java.util.logging.Logger.getLogger("org.mongodb.driver");
        java.util.logging.Logger morphiaLogger = java.util.logging.Logger.getLogger("org.mongodb.morphia");

        morphiaLogger.setLevel(Level.SEVERE);
        mongoLogger.setLevel(Level.SEVERE);
    }

    @Before
    public void prepare() {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase db = mongoClient.getDatabase("tests-db");
        db.drop();

        Constants.BLOCK_SIZE = 10;  // for testing purposes
    }

    @After
    public void teardown() {
        // clean-up
        MongoClient mongoClient = new MongoClient();
        MongoDatabase db = mongoClient.getDatabase("tests-db");
        db.drop();
    }

    @Test
    public void TrivialTest() {
        clientDB.addFile(file1);
        assertContainsAndNothingElse(file1);

        clientDB.addFile(file3);
        assertContainsAndNothingElse(file1, file3);

        clientDB.addFile(file3);
        assertContainsAndNothingElse(file1, file3);

        clientDB.addFile(file1);
        assertContainsAndNothingElse(file1, file3);

        clientDB.addFile(file2);
        clientDB.addFile(file4);
        assertContainsAndNothingElse(file1, file2, file3, file4);
    }

    @Test
    public void PersistenceTest() {
        /**
         * DatabaseProvider caches instances of ClientDBMongoImpl, meaning
         * that it's pointless to create two clients for checking persistence
         * - they will work with one instance of ClientDBMongoImpl.
         *
         * To really check persistence, we have to:
         *  1. Work with DB
         *  2. Make a new instance of ClientDBMongoImpl
         *  3. Check that new instance sees all changes
         */

        clientDB.addFile(file2);
        clientDB.addFile(file4);

        clientDB = new ClientDBMongoImpl("tests-db");
        assertContainsAndNothingElse(file2, file4);
    }

    @Test
    public void AddAllPartsTest() {
        clientDB.addAllPartsOfFile(file1);
        assertEquals(1, clientDB.listFileParts(0).size());

        FilePart part = clientDB.listFileParts(0).get(0);
        assertEquals(0, part.getFileID());
        assertEquals(0, part.getOffset());
        assertEquals(10, part.getSize());

        clientDB.addAllPartsOfFile(file3);
        assertEquals(23, clientDB.listFileParts(2).size());
        assertEquals(8, clientDB.listFileParts(2).get(22).getSize());
    }

    @Test
    public void AddPartOfFileTest() {
        clientDB.addPartOfFile(new FilePart(1, 40, 2));
        List<FilePart> fileParts = clientDB.listFileParts(1);

        assertEquals(1, fileParts.size());
        FilePart fp = fileParts.get(0);
        assertEquals(1, fp.getFileID());
        assertEquals(40, fp.getOffset());
        assertEquals(2, fp.getSize());
    }

    @Test
    public void GetFilePartTest() {
        clientDB.addAllPartsOfFile(file3);

        FilePart p1 = clientDB.getFilePart(2, 15);
        assertEquals(150, p1.getOffset());
        assertEquals(10, p1.getSize());

        FilePart p2 = clientDB.getFilePart(2, 22);
        assertEquals(220, p2.getOffset());
        assertEquals(8, p2.getSize());

        FilePart p3 = clientDB.getFilePart(2, 0);
        assertEquals(0, p3.getOffset());
        assertEquals(10, p3.getSize());
    }
    private void assertContainsAndNothingElse(FileEntity ... expected) {
        List<FileEntity> actual = clientDB.listSeededFiles();
        List<FileEntity> expectedList = Arrays.asList(expected);

        assertEquals(expectedList.size(), actual.size());

        HashSet<FileEntity> actualSet = new HashSet<>(actual);
        HashSet<FileEntity> expectedSet = new HashSet<>(expectedList);

        assertEquals(expectedSet, actualSet);
    }
}
