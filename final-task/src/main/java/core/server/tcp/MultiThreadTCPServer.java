package core.server.tcp;

import core.server.TaskExecutor;
import util.Log;
import util.Parameters;
import wire.Protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import static wire.WireMessages.Numbers;

public class MultiThreadTCPServer extends TCPServer {


    public MultiThreadTCPServer(Log log, Protocol protocol, TaskExecutor taskExecutor, Parameters params, boolean usesSingleConnection) {
        super(log, protocol, taskExecutor, params, usesSingleConnection);
    }

    @Override
    public void serverLoopBody() throws IOException {
        Socket clientSocket = serverSocket.accept();
        accepted((InetSocketAddress) clientSocket.getRemoteSocketAddress());

        Thread thread = new Thread(() -> {
            try {
                handleClient(clientSocket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    private void handleClient(Socket clientSocket) throws IOException {
        InputStream inputStream = clientSocket.getInputStream();
        OutputStream outputStream = clientSocket.getOutputStream();
        InetSocketAddress remoteSocketAddress = (InetSocketAddress) clientSocket.getRemoteSocketAddress();

        while (!clientSocket.isClosed()) {
            Numbers numbers = protocol.readMessage(inputStream);
            readFinished(remoteSocketAddress);
            if (numbers == null) {
                // Protobuf returns null when inputStream reached end,
                // i.e. when socket is closed
                return;
            }

            Numbers result = taskExecutor.executeTask(numbers);
            processingFinished(remoteSocketAddress);

            protocol.writeMessage(result, outputStream);
            writeFinished(remoteSocketAddress);
            accepted(remoteSocketAddress);
        }
    }
}
