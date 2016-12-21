package core.server.tcp;

import core.server.TaskExecutor;
import util.Log;
import util.Parameters;
import wire.Protocol;
import wire.WireMessages;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CachedThreadPoolServer extends TCPServer {
    private ExecutorService workersPool = Executors.newCachedThreadPool();

    public CachedThreadPoolServer(Log log, Protocol protocol, TaskExecutor taskExecutor, Parameters params, boolean usesSingleConnection) {
        super(log, protocol, taskExecutor, params, usesSingleConnection);
    }


    @Override
    public void shutdownServer() throws IOException {
        serverSocket.close();
        workersPool.shutdownNow();
    }

    @Override
    public void serverLoopBody() throws IOException {
        Socket clientSocket = serverSocket.accept();
        accepted((InetSocketAddress) clientSocket.getRemoteSocketAddress());

        workersPool.submit(() -> {
            try {
                handleClient(clientSocket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    private void handleClient(Socket clientSocket) throws IOException {
        InputStream inputStream = clientSocket.getInputStream();
        OutputStream outputStream = clientSocket.getOutputStream();
        InetSocketAddress remoteSocketAddress = (InetSocketAddress) clientSocket.getRemoteSocketAddress();

        while (!clientSocket.isClosed()) {
            WireMessages.Numbers numbers = protocol.readMessage(inputStream);
            readFinished(remoteSocketAddress);

            WireMessages.Numbers result = taskExecutor.executeTask(numbers);
            processingFinished(remoteSocketAddress);

            protocol.writeMessage(result, outputStream);
            writeFinished(remoteSocketAddress);

            // We make fake 'accepted' to refresh accepted-timestamp for architectures
            // with single connection
            accepted(remoteSocketAddress);
        }
    }
}

