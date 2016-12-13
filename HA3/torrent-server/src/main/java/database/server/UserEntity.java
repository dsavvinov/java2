package database.server;

import database.FileEntity;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

@Entity
public class UserEntity {
    @Id
    private String fullAddress;

    private short port;
    private String address;
    private List<FileEntity> seededFiles = new ArrayList<>();

    // Dummy ctor for MongoDB
    public UserEntity() { }

    public UserEntity(short port, String address) {
        this.port = port;
        this.address = address;
        fullAddress = address + "/" + port;
    }

    public short getPort() {
        return port;
    }

    public List<FileEntity> getSeededFiles() {
        return seededFiles;
    }

    public void setSeededFiles(List<FileEntity> update) {
        seededFiles = update;
    }

    public String getAddress() {
        return address;
    }

    public void updateFiles(List<FileEntity> files) {
        seededFiles = files;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserEntity that = (UserEntity) o;

        return fullAddress.equals(that.fullAddress);

    }

    @Override
    public int hashCode() {
        return fullAddress.hashCode();
    }
}
