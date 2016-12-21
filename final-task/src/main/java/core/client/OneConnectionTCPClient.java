package core.client;

import util.Log;
import util.Parameters;
import wire.Protocol;
import wire.WireMessages;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

class OneConnectionTCPClient extends AbstractClient {
    private Socket socket;
    private OutputStream outputStream;
    private InputStream inputStream;

    public OneConnectionTCPClient(Log log, int id, Protocol protocol, TasksProvider tasksProvider, Parameters parameters) {
        super(log, id, protocol, tasksProvider, parameters);
    }

    @Override
    public void beforeClass() throws IOException {
        log.trace(prefix + "establishing connection to server");
        socket = new Socket(parameters.serverHost, parameters.serverPort);
        outputStream = socket.getOutputStream();
        inputStream = socket.getInputStream();
        log.trace(prefix + "connected!");
    }

    @Override
    public void afterClass() throws IOException {
        log.trace(prefix + "closing socket");
        socket.close();
        log.trace(prefix + "closed socket");
    }

    @Override
    void sendMessage(WireMessages.Numbers msg) throws IOException {
        byte[] bytes = protocol.toBytes(msg);
        WireMessages.Numbers numbers = protocol.fromBytes(bytes);
        log.trace(prefix + "sent " + bytes.length + "bytes");
        protocol.writeMessage(msg, outputStream);
    }

    @Override
    WireMessages.Numbers receiveMessage() throws IOException {
        return protocol.readMessage(inputStream);
    }
}
