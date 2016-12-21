package core.server;

import java.io.IOException;

public interface Server {
    void start() throws IOException;
    void shutdown() throws IOException;
    double getAverageQueryTime();
    double getAverageClientTime();
}
