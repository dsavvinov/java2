package database.server;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;
import utils.Constants;

import java.util.Date;

/**
 * Seed is an abstraction of User, who seeds some file.
 * That means, that Seed instance is bound to some FileEntity
 * (seeded file), and it has a timestamp of last update
 * (because seeds can leave torrent)
 */
@Entity
public class Seed {
    @Id
    private ObjectId id;

    private Date updateTimestamp;

    @Reference
    private UserEntity user;

    public Date getUpdateTimestamp() {
        return updateTimestamp;
    }

    public UserEntity getUser() {
        return user;
    }

    // Dummy ctor for MongoDB
    public Seed() { }

    public Seed(Date updateTimestamp, UserEntity user) {
        this.updateTimestamp = updateTimestamp;
        this.user = user;
    }

    public boolean isOutdated() {
        Date curDate = new Date();
        long msSinceLastUpdate = curDate.getTime() - updateTimestamp.getTime();
        return msSinceLastUpdate > Constants.UPDATE_SOFT_TIMEOUT;
    }

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof Seed) ) {
            return false;
        }

        Seed other = (Seed) obj;
        return this.getUser().equals(other.getUser());
    }

    public void update() {
        updateTimestamp = new Date();
    }
}
