package core;

import core.server.InsertionSortExecutor;
import core.server.Server;
import core.server.ServersFactory;
import core.server.TaskExecutor;
import util.Log;
import util.Parameters;
import util.StandardLog;
import wire.ClientServerProtocol;
import wire.Protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ServerCLI {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("Provide server type, server address and port");
            return;
        }

        ServersFactory.ServerType type = ServersFactory.ServerType.forInt(Integer.parseInt(args[0]));

        Parameters params = new Parameters(
                /* delay = */ 100,
                /* retries = */ 10,
                /* serverHost = */ args[1],
                /* serverPort = */ Integer.parseInt(args[2]),
                /* arraySize = */ 10,
                /* fixedThreadPoolSize = */ 100
        );

        Protocol protocol = new ClientServerProtocol();
        TaskExecutor executor = new InsertionSortExecutor();
        Log log = new StandardLog();
        Server server = new ServersFactory(protocol, executor, log).createServer(type, params);

        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.println("Server started. Enter any string to shutdown.");
            String s = reader.readLine();
            server.shutdown();
            double averageClientTime = server.getAverageClientTime();
            double averageQueryTime = server.getAverageQueryTime();

            System.out.println("Average client time = " + averageClientTime);
            System.out.println("Average query time = " + averageQueryTime);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
