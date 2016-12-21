package core.server.udp;

import core.server.TaskExecutor;
import util.Log;
import util.Parameters;
import wire.Protocol;

public class MultipleThreadUDPServer extends UDPServer {


    public MultipleThreadUDPServer(Log log, Protocol protocol, TaskExecutor taskExecutor, Parameters params, boolean usesSingleConnection) {
        super(log, protocol, taskExecutor, params, usesSingleConnection);
    }

    @Override
    protected void scheduleMessageProcessing(Runnable messageProcessingTask) {
        Thread handlerThread = new Thread(messageProcessingTask);
        log.trace(prefix + "starting separate handler-thread");
        handlerThread.start();
    }
}
