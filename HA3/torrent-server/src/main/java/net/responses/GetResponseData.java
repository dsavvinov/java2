package net.responses;

import java.nio.channels.FileChannel;

public class GetResponseData {
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

    public GetResponseData(FileChannel fc, long offset, long size) {
        this.fc = fc;
        this.offset = offset;
        this.size = size;
    }
}
