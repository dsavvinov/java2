package database.client;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity
public class FilePart {
    private final int fileID;
    private final long offset;
    private final long size;

    @Id
    private final long partID;

    public FilePart() {
        fileID = 0;
        offset = 0;
        size = 0;
        partID = 0;
    }
    public FilePart(int fileID, long offset, long size) {
        this.fileID = fileID;
        this.offset = offset;
        this.size = size;
        partID = fileID * (1L << 32) + offset;
    }

    public int getFileID() {
        return fileID;
    }

    public long getOffset() {
        return offset;
    }

    public long getSize() {
        return size;
    }
}
