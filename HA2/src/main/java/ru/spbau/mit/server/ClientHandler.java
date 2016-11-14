package ru.spbau.mit.server;


import ru.spbau.mit.io.Logger;
import ru.spbau.mit.net.Query;
import ru.spbau.mit.net.TypedSocket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

import static ru.spbau.mit.net.Query.GET_CMD;
import static ru.spbau.mit.net.Query.LIST_CMD;

public class ClientHandler implements Runnable{
    private final Logger log;
    private final SocketChannel clientSocket;
    private String handlerName = "NOT_INITIALIZED";
    public ClientHandler(Logger log, SocketChannel clientSocket) {
        this.log = log;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            TypedSocket typedSocket = new TypedSocket(clientSocket);
            handlerName = "Handler <" + clientSocket.getRemoteAddress().toString() + ">";

            log.trace(handlerName + ":  connection established, reading query");
            Query query = (Query) typedSocket.readObject(Query.class);

            switch (query.getName()) {
                case LIST_CMD:
                    log.trace(handlerName + ": Processing <List> command");
                    ArrayList<Item> files = Storage.getFilesList(query.getArg());
                    typedSocket.writeObject(files);
                    log.trace(handlerName + ": Sent files list");
                    clientSocket.close();
                    break;

                case GET_CMD:
                    String fileName = query.getArg();
                    log.trace(handlerName + ": Processing <Get " + fileName + ">");

                    Path filePath = Storage.getAbsolutePath(fileName);
                    if (filePath == null) {
                        log.error(handlerName + ": Error - no file <" + fileName + "> found");
                        typedSocket.writeString("File not found!");
                        clientSocket.close();
                        break;
                    }

                    log.trace(handlerName + ": file is found, sending OK response");
                    typedSocket.writeString("OK");
                    log.trace(handlerName + ": sent OK");

                    FileChannel fc = FileChannel.open(filePath, StandardOpenOption.READ);
                    ByteBuffer buffer = ByteBuffer.allocate(1000000);
                    log.trace(handlerName + ": Starting file <" + fileName + "> transmission");
                    while (fc.read(buffer) > 0) {
                        buffer.flip();
                        clientSocket.write(buffer);
                        buffer.clear();
                    }
                    log.trace(handlerName + ": File <" + fileName + "> uploaded successfully");
                    clientSocket.close();
                    log.trace(handlerName + ": client socket shut down gracefully");
            }
        } catch (IOException e) {
            log.error(handlerName + ": error, " + e.toString());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                log.error(handlerName + ": error during closing socket " + e.toString());
            }
        }
    }
}
