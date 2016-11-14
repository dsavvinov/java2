package ru.spbau.mit.server;

public class Item {
    private final String path;
    private final Boolean directory;

    // Dummy default ctor for Jackson
    public Item() {
        directory = null;
        path = null;
    }

    public Item(String path, Boolean directory) {
        this.path = path;
        this.directory = directory;
    }

    public String getPath() {
        return path;
    }

    public Boolean isDirectory() {
        return directory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        if (path != null ? !path.equals(item.path) : item.path != null) return false;
        return directory != null ? directory.equals(item.directory) : item.directory == null;

    }

    @Override
    public int hashCode() {
        int result = path != null ? path.hashCode() : 0;
        result = 31 * result + (directory != null ? directory.hashCode() : 0);
        return result;
    }
}
