package database.client;

import com.mongodb.MongoClient;
import database.FileEntity;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;
import utils.Constants;

import java.util.List;

public class ClientDBMongoImpl implements ClientDatabase {
    private final Morphia morphia;
    private final Datastore datastore;

    public ClientDBMongoImpl(String dbName) {
        morphia = new Morphia();
        morphia.mapPackage("core.database.client");

        datastore = morphia.createDatastore(new MongoClient("localhost"), dbName);
        datastore.ensureIndexes();
    }


    @Override
    public List<FileEntity> listSeededFiles() {
        Query<FileEntity> query = datastore.createQuery(FileEntity.class);
        List<FileEntity> files = query.asList();
        return files;
    }

    @Override
    public void addFile(FileEntity file) {
        datastore.save(file);
    }

    @Override
    public List<FilePart> listFileParts(int id) {
        Query<FilePart> query = datastore.createQuery(FilePart.class);
        return query.field("fileID").equal(id).asList();
    }

    @Override
    public void addAllPartsOfFile(FileEntity file) {
        long curOffset = 0;
        while (curOffset < file.getSize()) {
            long size = Math.min(Constants.BLOCK_SIZE, file.getSize() - curOffset);
            datastore.save(new FilePart(file.getId(), curOffset, size));
            curOffset += Constants.BLOCK_SIZE;
        }
    }

    @Override
    public void addPartOfFile(FilePart part) {
        datastore.save(part);
    }

    @Override
    public FileEntity getFile(int id) {
        Query<FileEntity> query = datastore.createQuery(FileEntity.class);
        List<FileEntity> files = query.field("id").equal(id).asList();
        if (files.size() == 0) {
            return null;
        }

        if (files.size() > 1) {
            throw new InternalError("Got more than 1 FileEntities for ID = " + id);
        }

        return files.get(0);
    }

    @Override
    public FilePart getFilePart(int id, int part) {
        Query<FilePart> query = datastore.createQuery(FilePart.class);
        List<FilePart> files = query
                .field("fileID").equal(id)
                .field("offset").equal(part * Constants.BLOCK_SIZE).asList();
        if (files.size() == 0) {
            return null;
        }

        if (files.size() > 1) {
            throw new InternalError("Got more that 1 FilePart for ID = " + id + " , part = " + part);
        }

        return files.get(0);
    }
}
