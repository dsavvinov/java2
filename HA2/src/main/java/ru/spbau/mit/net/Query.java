package ru.spbau.mit.net;

public class Query {
    public static final String LIST_CMD = "list";
    public static final String GET_CMD = "get";

    private final String name;
    private String file = null;

    public Query() {
        name = null;
        file = null;
    }

    public Query(String name) {
        this.name = name;
    }

    public Query(String name, String file) {
        this(name);
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public String getFile() {
        return file;
    }
}
