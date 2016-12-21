package core.server.stat;

public class StatisticsHolder {
    public volatile long acceptedTimestamp;
    public volatile long readTimestamp;
    public volatile long sortedTimestamp;
    public volatile long wroteTimestamp;

    public volatile long totalClientTime = 0;
    public volatile long totalQueryTime = 0;
    public volatile int queries = 0;

    public void addClientTime() {
        totalClientTime += sortedTimestamp - readTimestamp ;
    }

    public void addQueryTime(long from) {
        totalQueryTime += wroteTimestamp - from;
    }

    public void addQueryTime() {
        totalQueryTime += wroteTimestamp - acceptedTimestamp;
    }
}
