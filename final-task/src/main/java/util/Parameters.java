package util;

public class Parameters {
    public final int delay;
    public final int retries;
    public final String serverHost;
    public final int serverPort;
    public final int arraySize;

    // For nio-server
    public final int fixedThreadPoolSize;

    public Parameters(int delay, int retries, String serverHost, int serverPort, int arraySize, int fixedThreadPoolSize) {
        this.delay = delay;
        this.retries = retries;
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.arraySize = arraySize;
        this.fixedThreadPoolSize = fixedThreadPoolSize;
    }

    public Parameters plus(Parameters delta) {
        return new Parameters(
                delay + delta.delay,
                retries + delta.retries,
                serverHost,
                serverPort,
                arraySize + delta.arraySize,
                fixedThreadPoolSize
                );
    }
}
