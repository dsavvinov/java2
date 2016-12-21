package core.client;

import util.Log;
import util.Parameters;
import wire.Protocol;
import wire.WireMessages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

class ManyConnectionsUDPClient extends AbstractClient {
    private InetAddress serverAddress;
    private DatagramSocket socket;
    private int messageSize;

    public ManyConnectionsUDPClient(Log log, int id, Protocol protocol, TasksProvider tasksProvider, Parameters parameters) {
        super(log, id, protocol, tasksProvider, parameters);
    }

    @Override
    protected void beforeClass() throws IOException {
        serverAddress = InetAddress.getByName(parameters.serverHost);
    }

    @Override
    public void beforeIteration() throws IOException {
        socket = new DatagramSocket();
        socket.setSoTimeout(1000);
    }

    @Override
    public void afterIteration() throws IOException {
        socket.close();
    }

    @Override
    void sendMessage(WireMessages.Numbers msg) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        protocol.writeMessage(msg, baos);
        byte[] msgBytes = baos.toByteArray();

        // Save message size for later use in receiveMessage
        messageSize = msgBytes.length;

        DatagramPacket packet = new DatagramPacket(msgBytes, msgBytes.length, serverAddress, parameters.serverPort);
        socket.send(packet);
    }

    @Override
    WireMessages.Numbers receiveMessage() throws IOException {
        byte[] responseBytes = new byte[messageSize * 3];
        DatagramPacket responsePacket = new DatagramPacket(responseBytes, responseBytes.length);
        try {
            socket.receive(responsePacket);
        } catch (SocketTimeoutException ignored) {
            return null;
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(responsePacket.getData());
        return protocol.readMessage(bais);
    }


}
