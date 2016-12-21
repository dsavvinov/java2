package core.server;

import core.server.stat.StatisticsRecorder;
import core.server.stat.StatisticsRecorderImpl;
import util.Log;
import util.Parameters;
import wire.Protocol;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;

/**
 * Any action in launchSever() happens-before serverLoopBody()
 */
abstract public class AbstractServer implements Server {
    private Thread serverThread;
    private StatisticsRecorder statRecorder;
    private volatile boolean shutdown;

    protected final Log log;
    protected final String prefix;
    protected final Protocol protocol;
    protected final TaskExecutor taskExecutor;
    protected final Parameters params;

    abstract public void serverLoopBody() throws IOException;
    abstract public void launchServer() throws IOException;
    abstract public void shutdownServer() throws IOException;

    public AbstractServer(Log log, Protocol protocol, TaskExecutor taskExecutor, Parameters params, boolean usesSingleConnection) {
        this.log = log;
        this.protocol = protocol;
        this.taskExecutor = taskExecutor;
        this.params = params;
        this.statRecorder = new StatisticsRecorderImpl(usesSingleConnection);
        prefix = "[Server] ";
    }

    @Override
    public void start() throws IOException {
        launchServer();
        serverThread = new Thread(this::serverLoop);
        serverThread.start();
    }

    @Override
    public void shutdown() throws IOException {
        log.trace(prefix + "shutting down");
        shutdown = true;
        serverThread.interrupt();

        while (serverThread.isAlive()) {
            try {
                serverThread.join();
            } catch (InterruptedException ignored) { }
        }
        shutdownServer();
        log.trace(prefix + "server thread terminated successfully!");
    }

    @Override
    public double getAverageQueryTime() {
        return statRecorder.getAverageQueryTime();
    }

    @Override
    public double getAverageClientTime() {
        return statRecorder.getAverageClientTime();
    }

    protected void accepted(InetSocketAddress client) {
        log.trace(prefix + "accepted client <" + client.toString() + ">");
        statRecorder.accepted(client);
    }

    protected void readFinished(InetSocketAddress client) {
        log.trace(prefix + "finished reading message from client <" + client.toString() + ">");
        statRecorder.readFinished(client);
    }

    protected void processingFinished(InetSocketAddress client) {
        log.trace(prefix + "finished processing reply for client <" + client.toString() + ">");
        statRecorder.processingFinished(client);
    }

    protected void writeFinished(InetSocketAddress client) {
        log.trace(prefix + "finished writing reply for client <" + client.toString() + ">");
        statRecorder.writeFinished(client);
    }

    private void serverLoop() {
        try {
            while (!shutdown) {
                try {
                    serverLoopBody();
                } catch (SocketTimeoutException ignored) {
                    // We ignore timeouts because we need them solely for checking
                    // `shutdown` flag from time to time
                }
            }
        } catch (IOException e) {
            log.error(prefix + "Error: " + e.getMessage());
        }
    }
}
