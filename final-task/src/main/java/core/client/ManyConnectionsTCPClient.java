package core.client;

import util.Log;
import util.Parameters;
import wire.Protocol;
import wire.WireMessages;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

class ManyConnectionsTCPClient extends AbstractClient {
    private Socket socket;
    private OutputStream outputStream;
    private InputStream inputStream;

    public ManyConnectionsTCPClient(Log log, int id, Protocol protocol, TasksProvider tasksProvider, Parameters parameters) {
        super(log, id, protocol, tasksProvider, parameters);
    }

    @Override
    public void beforeIteration() throws IOException {
        socket = new Socket(parameters.serverHost, parameters.serverPort);
        outputStream = socket.getOutputStream();
        inputStream = socket.getInputStream();
    }

    @Override
    public void afterIteration() throws IOException {
        socket.close();
    }

    @Override
    void sendMessage(WireMessages.Numbers msg) throws IOException {
        protocol.writeMessage(msg, outputStream);
    }

    @Override
    WireMessages.Numbers receiveMessage() throws IOException {
        return protocol.readMessage(inputStream);
    }
}
