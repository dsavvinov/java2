package ru.spbau.mit.net;

public class Query {
    public static final String LIST_CMD = "list";
    public static final String GET_CMD = "get";

    private final String name;
    private String arg = null;

    // Dummy default ctor fot Jackson
    public Query() {
        name = null;
        arg = null;
    }

    public Query(String name) {
        this.name = name;
    }

    public Query(String name, String file) {
        this(name);
        this.arg = file;
    }

    public String getName() {
        return name;
    }

    public String getArg() {
        return arg;
    }
}
