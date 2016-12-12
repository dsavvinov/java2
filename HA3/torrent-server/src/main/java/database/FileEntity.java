package database;

import database.server.Seed;
import database.server.UserEntity;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.nio.file.Path;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Entity
public class FileEntity {
    @Id
    private final int id;
    private final String name;
    private long size;

    private List<Seed> seededBy = new LinkedList<>();

    // Used for seeds-db. Records in server DB dont have this field set.
    private String localPath;

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public FileEntity(int id, String name, long size) {
        this.id = id;
        this.name = name;
        this.size = size;
    }

    public void addSeed(UserEntity user) {
        Date timestamp = new Date();
        Seed seed = new Seed(timestamp, user);
        seededBy.add(seed);
    }

    public List<Seed> getSeeds() {
        return seededBy;
    }

    // dummy ctor, somehow needed to MongoDB to work properly
    public FileEntity() {
        id = -1;
        name = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileEntity that = (FileEntity) o;

        return id == that.id;

    }

    @Override
    public int hashCode() {
        return id;
    }
}
