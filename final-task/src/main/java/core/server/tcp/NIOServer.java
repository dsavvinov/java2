package core.server.tcp;

import core.server.AbstractServer;
import core.server.TaskExecutor;
import util.Log;
import util.Parameters;
import wire.MessagesParser;
import wire.Protocol;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static wire.WireMessages.*;

public class NIOServer extends AbstractServer {
    private ServerSocketChannel serverChannel;
    private Selector selector;
    private boolean isBlocking;
    private ExecutorService workersPool;

    public NIOServer(Log log, Protocol protocol, TaskExecutor taskExecutor, Parameters params, boolean singleConnection, boolean isBlocking) {
        super(log, protocol, taskExecutor, params, singleConnection);
        this.isBlocking = isBlocking;
        workersPool = Executors.newFixedThreadPool(params.fixedThreadPoolSize);
    }

    @Override
    public void launchServer() throws IOException {
        selector = Selector.open();
        serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(params.serverPort));
        serverChannel.configureBlocking(isBlocking);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    @Override
    public void shutdownServer() throws IOException {
        serverChannel.close();
        workersPool.shutdownNow();
    }

    @Override
    public void serverLoopBody() throws IOException {
        selector.select();

        Set<SelectionKey> selectionKeys = selector.selectedKeys();
        Iterator<SelectionKey> iterator = selectionKeys.iterator();
        while(iterator.hasNext()) {
            SelectionKey selectionKey = iterator.next();
            iterator.remove();

            if (!selectionKey.isValid()) {
                continue;
            }

            if (selectionKey.isAcceptable()) {
                // serverChannel is the only channel on selector that listens for ACCEPT
                SocketChannel clientChannel = serverChannel.accept();
                accepted((InetSocketAddress) clientChannel.getRemoteAddress());

                clientChannel.configureBlocking(false);
                clientChannel.register(selector, SelectionKey.OP_READ, new MessagesParser(params.arraySize * 16, protocol));
            } else if (selectionKey.isReadable()) {
                log.trace(prefix + "found readable key: " + selectionKey.channel().toString());
                tryReadMessage(selectionKey);
            }
        }
    }

    private void tryReadMessage(SelectionKey key) throws IOException {
        MessagesParser parser = (MessagesParser) key.attachment();
        final SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(10000);
        int read = channel.read(buffer);

        if (read == -1) {
            // Channel is closed from the client side, close from ours too
            log.trace(prefix + "client <" + channel.getRemoteAddress().toString() + "> " +
                    "closed channel, closing too");
            channel.close();
            return;
        }

        // Read while we have something to read
        while(read != 0) {
            buffer.flip();
            byte[] b = new byte[buffer.remaining()];
            buffer.get(b);

            parser.put(b);
            buffer.compact();
            read = channel.read(buffer);
        }

        final Numbers numbers = parser.get();
        if (numbers == null) {
            log.trace(prefix + "incomplete message");
            return;
        }
        readFinished((InetSocketAddress) channel.getRemoteAddress());

        workersPool.submit(() -> {
            try {
                executeMessage(numbers, channel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void executeMessage(Numbers numbers, SocketChannel channel) throws IOException {
        InetSocketAddress remoteAddress = (InetSocketAddress) channel.getRemoteAddress();
        Numbers result = taskExecutor.executeTask(numbers);
        processingFinished(remoteAddress);

        byte[] resultBytes = protocol.toBytes(result);
        ByteBuffer resultBuffer = ByteBuffer.wrap(resultBytes);

        int bytesWrote = 0;
        while(bytesWrote < resultBytes.length) {
            bytesWrote += channel.write(resultBuffer);
        }
        writeFinished(remoteAddress);
        accepted(remoteAddress);
    }
}
