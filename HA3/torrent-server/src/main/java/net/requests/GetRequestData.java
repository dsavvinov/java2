package net.requests;

public class GetRequestData {
    private final int id;
    private final int part;

    public GetRequestData(int id, int part) {
        this.id = id;
        this.part = part;
    }

    public int getId() {
        return id;
    }

    public int getPart() {
        return part;
    }
}
