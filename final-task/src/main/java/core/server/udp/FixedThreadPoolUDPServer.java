package core.server.udp;

import core.server.TaskExecutor;
import util.Log;
import util.Parameters;
import wire.Protocol;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FixedThreadPoolUDPServer extends UDPServer {
    private final ExecutorService workersPool;

    public FixedThreadPoolUDPServer(Log log, Protocol protocol, TaskExecutor taskExecutor, Parameters params, boolean singleConnection) {
        super(log, protocol, taskExecutor, params, singleConnection);
        workersPool = Executors.newFixedThreadPool(params.fixedThreadPoolSize);
    }

    @Override
    public void shutdownServer() throws IOException {
        serverSocket.close();
        workersPool.shutdownNow();
    }

    @Override
    protected void scheduleMessageProcessing(Runnable messageProcessingTask) {
        workersPool.submit(messageProcessingTask);
    }
}
