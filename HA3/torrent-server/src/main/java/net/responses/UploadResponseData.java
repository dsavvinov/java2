package net.responses;

public class UploadResponseData {
    public int getId() {
        return id;
    }

    private final int id;

    public UploadResponseData(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return Integer.toString(id);
    }
}
