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

public class SingleThreadTCPServer extends TCPServer {


    public SingleThreadTCPServer(Log log, Protocol protocol, TaskExecutor taskExecutor, Parameters params, boolean usesSingleConnection) {
        super(log, protocol, taskExecutor, params, usesSingleConnection);
    }

    @Override
    public void serverLoopBody() throws IOException {
        Socket clientSocket = serverSocket.accept();
        accepted((InetSocketAddress) clientSocket.getRemoteSocketAddress());
        handleClient(clientSocket);
    }

    private void handleClient(Socket clientSocket) throws IOException {
        InputStream inputStream = clientSocket.getInputStream();
        OutputStream outputStream = clientSocket.getOutputStream();
        InetSocketAddress remoteSocketAddress = (InetSocketAddress) clientSocket.getRemoteSocketAddress();

        WireMessages.Numbers numbers = protocol.readMessage(inputStream);
        readFinished(remoteSocketAddress);

        WireMessages.Numbers result = taskExecutor.executeTask(numbers);
        processingFinished(remoteSocketAddress);

        protocol.writeMessage(result, outputStream);
        writeFinished(remoteSocketAddress);

        clientSocket.close();
    }
}
