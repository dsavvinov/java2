package core.server.tcp;

import core.server.AbstractServer;
import core.server.TaskExecutor;
import util.Log;
import util.Parameters;
import wire.Protocol;

import java.io.IOException;
import java.net.ServerSocket;

abstract class TCPServer extends AbstractServer {
    protected ServerSocket serverSocket;

    public TCPServer(Log log, Protocol protocol, TaskExecutor taskExecutor, Parameters params, boolean usesSingleConnection) {
        super(log, protocol, taskExecutor, params, usesSingleConnection);
    }


    @Override
    public void launchServer() throws IOException {
        serverSocket = new ServerSocket(params.serverPort);
        serverSocket.setSoTimeout(5000);    // don't block for too long to be able to shutdown
    }

    @Override
    public void shutdownServer() throws IOException {
        serverSocket.close();
    }
}
