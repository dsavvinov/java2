package core.server.stat;

import java.net.InetSocketAddress;

public interface StatisticsRecorder {
    void accepted(InetSocketAddress client);
    void readFinished(InetSocketAddress client);
    void processingFinished(InetSocketAddress client);
    void writeFinished(InetSocketAddress client);

    double getAverageClientTime();
    double getAverageQueryTime();
}
