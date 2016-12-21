package core.server.udp;

import core.server.AbstractServer;
import core.server.TaskExecutor;
import util.Log;
import util.Parameters;
import wire.Protocol;
import wire.WireMessages;

import java.io.IOException;
import java.net.*;

abstract public class UDPServer extends AbstractServer {
    protected DatagramSocket serverSocket;
    protected DatagramPacket packet;

    public UDPServer(Log log, Protocol protocol, TaskExecutor taskExecutor, Parameters params, boolean singleConnection) {
        super(log, protocol, taskExecutor, params, singleConnection);
    }


    abstract protected void scheduleMessageProcessing(Runnable messageProcessingTask);

    @Override
    public void launchServer() throws IOException {
        serverSocket = new DatagramSocket(params.serverPort);
        serverSocket.setSoTimeout(2000);
        byte[] buffer = new byte[params.arraySize * 16];
        packet = new DatagramPacket(buffer, buffer.length);
        log.trace(prefix + "listening for messages...");
    }

    @Override
    public void shutdownServer() throws IOException {
        serverSocket.close();
    }

    @Override
    public void serverLoopBody() throws IOException {
        try {
            serverSocket.receive(packet);
        } catch (SocketTimeoutException ignored) {
            return;
        }
        accepted((InetSocketAddress) packet.getSocketAddress());
        readFinished((InetSocketAddress) packet.getSocketAddress());

        final byte[] data = packet.getData().clone();
        final SocketAddress address = packet.getSocketAddress();

        scheduleMessageProcessing(() -> {
            try {
                WireMessages.Numbers numbers = protocol.fromBytes(data);

                WireMessages.Numbers result = taskExecutor.executeTask(numbers);
                processingFinished((InetSocketAddress) address);
                byte[] resultBytes = protocol.toBytes(result);

                DatagramPacket response = new DatagramPacket(resultBytes, resultBytes.length, address);
                serverSocket.send(response);
                writeFinished((InetSocketAddress) address);
            } catch (IOException e) {
                log.error(prefix + "error while handling packer from user " +
                        "<" + packet.getSocketAddress().toString() + ">" +
                        ": " + e.getMessage()
                );
            }
        });
    }
}
