package database;

import database.client.ClientDBMongoImpl;
import database.client.ClientDatabase;
import database.server.ServerDBMongoImpl;
import database.server.ServerDatabase;

public class DatabaseProvider {
    // TODO: implement properly, check aliveness
    private volatile static ClientDatabase clientDatabase = null;
    private volatile static ServerDatabase serverDatabase = null;

    public static ServerDatabase getServerDB() {
       return getServerDB("torrent-server-db");
    }

    public static ServerDatabase getServerDB(String dbName) {
        if (serverDatabase == null) {
            synchronized (ServerDatabase.class) {
                serverDatabase = new ServerDBMongoImpl(dbName);
            }
        }
        return serverDatabase;
    }


    public static ClientDatabase getClientDB() {
        return getClientDB("torrent-client-db");
    }

    public static ClientDatabase getClientDB(String dbName) {
        if (clientDatabase == null) {
            synchronized (ClientDatabase.class) {
                if (clientDatabase == null) {
                    clientDatabase = new ClientDBMongoImpl(dbName);
                }
            }
        }
        return clientDatabase;
    }
}
