package net.queries.responses;

import java.nio.channels.FileChannel;

public class GetResponse {
    private FileChannel fc;
    private long offset;
    private long size;

    public FileChannel getFileChannel() {
        return fc;
    }

    public long getOffset() {
        return offset;
    }

    public long getSize() {
        return size;
    }

    public GetResponse() { }

    public GetResponse(FileChannel fc, long offset, long size) {
        this.fc = fc;
        this.offset = offset;
        this.size = size;
    }
}
