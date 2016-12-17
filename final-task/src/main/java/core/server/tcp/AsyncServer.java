package core.server.tcp;

import core.server.AbstractServer;
import core.server.TaskExecutor;
import util.Log;
import util.Parameters;
import wire.MessagesParser;
import wire.Protocol;
import wire.WireMessages;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AsyncServer extends AbstractServer {

    private AsynchronousServerSocketChannel serverChannel;

    public AsyncServer(Log log, Protocol protocol, TaskExecutor taskExecutor, Parameters params, boolean usesSingleConnection) {
        super(log, protocol, taskExecutor, params, usesSingleConnection);
    }


    // Note that we don't do anything in server loop body, because accept-loop
    // will be maintained via async handlers
    // We need it though, for server to be alive and not finish prematurely
    @Override
    public void serverLoopBody() throws IOException {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException ignored) { }
    }

    @Override
    public void launchServer() throws IOException {
        serverChannel = AsynchronousServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(params.serverPort));
        serverChannel.accept(new Attachment(
                new MessagesParser(params.arraySize * 16, protocol),
                ByteBuffer.allocate(params.arraySize * 16)), new AcceptHandler());
    }

    @Override
    public void shutdownServer() throws IOException {
        serverChannel.close();
    }

    private static class Attachment {
        public final MessagesParser parser;
        public volatile ByteBuffer buffer;
        public volatile AsynchronousSocketChannel channel;

        private Attachment(MessagesParser parser, ByteBuffer buffer) {
            this.parser = parser;
            this.buffer = buffer;
        }
    }

    private class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel, Attachment> {
        @Override
        public void completed(AsynchronousSocketChannel result, Attachment attachment) {
            try {
                accepted((InetSocketAddress) result.getRemoteAddress());
            } catch (IOException e) {
                log.error(prefix + "error in AcceptHandler: " + e.getMessage());
                return;
            }

            attachment.buffer = ByteBuffer.allocate(params.arraySize * 16);
            attachment.channel = result;
            result.read(attachment.buffer, attachment, new ReadHandler());

            serverChannel.accept(new Attachment(
                    new MessagesParser(params.arraySize * 16, protocol),
                    ByteBuffer.allocate(params.arraySize * 16)), new AcceptHandler()
            );
        }

        @Override
        public void failed(Throwable exc, Attachment attachment) {
            if (exc instanceof AsynchronousCloseException) {
                // Most probably this is ok, as someone shut down the server and
                // all blocked async accepts threw that.
                // We will log it as 'trace' nevertheless, just in case.
                log.trace(prefix + "error while accepting: " + exc.toString());
                return;
            }
            log.error(prefix + "error while accepting: " + exc.toString());
        }
    }

    private class ReadHandler implements CompletionHandler<Integer, Attachment> {
        @Override
        public void completed(Integer result, Attachment attachment) {
            if (result == -1) {
                log.trace(prefix + "client closed connection, closing too");
                try {
                    attachment.channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }

            attachment.buffer.flip();
            byte[] bytes = new byte[attachment.buffer.remaining()];
            attachment.buffer.get(bytes);
            attachment.buffer.compact();

            attachment.parser.put(bytes);
            WireMessages.Numbers numbers = attachment.parser.get();
            if (numbers == null) {
                attachment.channel.read(attachment.buffer, attachment, this);
                return;
            }

            try {
                readFinished((InetSocketAddress) attachment.channel.getRemoteAddress());

                WireMessages.Numbers taskResult = taskExecutor.executeTask(numbers);
                processingFinished((InetSocketAddress) attachment.channel.getRemoteAddress());

                attachment.buffer = ByteBuffer.wrap(protocol.toBytes(taskResult));

                attachment.channel.write(attachment.buffer, attachment, new WriteHandler());
            } catch (IOException e) {
                log.error(prefix + "error in ReadHandler: " + e.getMessage());
            }
        }

        @Override
        public void failed(Throwable exc, Attachment attachment) {
            log.error(prefix + "error while reading: " + exc.getMessage());
        }
    }

    private class WriteHandler implements CompletionHandler<Integer, Attachment> {
        @Override
        public void completed(Integer result, Attachment attachment) {
            if (attachment.buffer.remaining() == 0) {
                try {
                    writeFinished((InetSocketAddress) attachment.channel.getRemoteAddress());
                    // Schedule another read in case if client uses single connection
                    attachment.buffer = ByteBuffer.allocate(params.arraySize * 16);
                    attachment.channel.read(attachment.buffer, attachment, new ReadHandler());
                    return;
                } catch (IOException e) {
                    log.error(prefix + "error in WriteHandler: " + e.getMessage());
                }
            }
            attachment.channel.write(attachment.buffer, attachment, this);
        }

        @Override
        public void failed(Throwable exc, Attachment attachment) {
            log.error(prefix + "error while writing <" + exc.getMessage());
        }
    }
}
