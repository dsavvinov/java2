package net.requests;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class UploadRequestData {
    private final String name;

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    private final long size;

    public UploadRequestData(String name, long size) {
        this.name = name;
        this.size = size;
    }

    @Override
    public String toString() {
        return "name = " + name + ", size = " + Long.toString(size);
    }
}
