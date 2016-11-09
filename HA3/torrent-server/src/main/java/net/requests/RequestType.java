package net.requests;

public enum RequestType {
    LIST(1),
    UPLOAD(2),
    SOURCES(3),
    UPDATE(4),
    STAT(1),
    GET(2),
    UNKNOWN(0);

    public int getId() {
        return id;
    }

    private int id;
    RequestType(int id) {
        this.id = id;
    }

    public static RequestType forInt(int id, boolean isP2P) {
        if (isP2P) {
            id += 4;
        }

        switch(id) {
            case 1: return LIST;
            case 2: return UPLOAD;
            case 3: return SOURCES;
            case 4: return UPDATE;
            case 5: return STAT;
            case 6: return GET;
            default: return UNKNOWN;
        }
    }
}
