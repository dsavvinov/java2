package database.server;

import database.DatabaseProvider;

/**
 * Stateful class for assigning ids
 */
public class IDManager {
    private static LastID lastID = null;

    public static int get() {
        if (lastID == null) {
            lastID = DatabaseProvider.getServerDB().getLastID();
        }

        int oldId = lastID.value;
        lastID.value++;

        return oldId;
    }
}
