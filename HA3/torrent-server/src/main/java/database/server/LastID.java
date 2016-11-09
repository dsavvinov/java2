package database.server;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;

@Entity
public class LastID {
    private ObjectId mongoId;
    public int value;
}
