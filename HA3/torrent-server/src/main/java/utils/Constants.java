package utils;

/**
 * Catalogue of application-wide constants
 */
public class Constants {
    public static final String SERVER_ADDRESS = "localhost";
    public static final int SERVER_PORT = 8081;

    public static final long UPDATE_HARD_TIMEOUT = 5 * 60 * 1000; // 5 min in ms
    public static final long UPDATE_SOFT_TIMEOUT = UPDATE_HARD_TIMEOUT / 2; // twice is often, to be safe

    public static long BLOCK_SIZE = 10 * 1024 * 1024;

    // Wire Ids
    public static final int LIST_ID = 1;
    public static final int UPLOAD_ID = 2;
    public static final int SOURCES_ID = 3;
    public static final int UPDATE_ID = 4;

    public static final int STAT_ID = 1;
    public static final int GET_ID = 2;



}
