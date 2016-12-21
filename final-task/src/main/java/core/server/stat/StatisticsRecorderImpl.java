package core.server.stat;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StatisticsRecorderImpl implements StatisticsRecorder {
    private final boolean usesSingleConnection;
    Map<InetSocketAddress, StatisticsHolder> clientsStatistics = new ConcurrentHashMap<>();

    public StatisticsRecorderImpl(boolean usesSingleConnection) {
        this.usesSingleConnection = usesSingleConnection;
    }

    @Override
    public void accepted(InetSocketAddress client) {
        StatisticsHolder holder = clientsStatistics.get(client);
        if (holder == null) {
            holder = new StatisticsHolder();
        }
        holder.acceptedTimestamp = System.currentTimeMillis();
        clientsStatistics.put(client, holder);
    }

    @Override
    public void readFinished(InetSocketAddress client) {
        StatisticsHolder holder = clientsStatistics.get(client);
        holder.readTimestamp = System.currentTimeMillis();
        holder.queries++;
        clientsStatistics.put(client, holder);
    }

    @Override
    public void processingFinished(InetSocketAddress client) {
        StatisticsHolder holder = clientsStatistics.get(client);
        holder.sortedTimestamp = System.currentTimeMillis();
        holder.addClientTime();
        clientsStatistics.put(client, holder);
    }

    @Override
    public void writeFinished(InetSocketAddress client) {
        StatisticsHolder holder = clientsStatistics.get(client);
        long lastRead = holder.readTimestamp;
        holder.wroteTimestamp = System.currentTimeMillis();

        if (!usesSingleConnection || lastRead == 0) {
            // If we use multiple connections, or if this is the first write, then
            // accept-timestamp is valid, so use it
            holder.addQueryTime();
        } else {
            // Otherwise, accept-timestamp is not valid, so use timestamp
            // of last read
            holder.addQueryTime(lastRead);
        }
    }

    @Override
    public double getAverageClientTime() {
        double sumOfPerClientAverages = 0;
        for (Map.Entry<InetSocketAddress, StatisticsHolder> entry : clientsStatistics.entrySet()) {
            StatisticsHolder holder = entry.getValue();
            sumOfPerClientAverages += (double) holder.totalClientTime / holder.queries;
        }
        return sumOfPerClientAverages / clientsStatistics.size();
    }

    @Override
    public double getAverageQueryTime() {
        double sumOfPerClientAverages = 0;
        for (Map.Entry<InetSocketAddress, StatisticsHolder> entry : clientsStatistics.entrySet()) {
            StatisticsHolder holder = entry.getValue();
            sumOfPerClientAverages += (double) holder.totalQueryTime / holder.queries;
        }
        return sumOfPerClientAverages / clientsStatistics.size();
    }
}
