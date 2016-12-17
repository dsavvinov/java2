package core;

import core.client.Client;
import core.client.ClientsFactory;
import core.client.TasksProvider;
import core.client.TasksProviderPRN;
import util.Log;
import util.Parameters;
import util.StandardLog;
import wire.ClientServerProtocol;
import wire.Protocol;

import java.io.IOException;
import java.util.Random;

public class ClientCLI {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Not enough arguments:\n" +
                    "   Usage: ./client <PROTOCOL> [CONNECTIONS]");
            return;
        }

        ClientsFactory.ClientType type;
        if (args[0].equals("udp")) {
            type = ClientsFactory.ClientType.MULTIPLE_UDP;
        } else if (args[0].equals("tcp") && args[1].equals("single")) {
            type = ClientsFactory.ClientType.SINGLE_TCP;
        } else if (args[0].equals("tcp") && args[1].equals("multiple")) {
            type = ClientsFactory.ClientType.MULTIPLE_TCP;
        } else {
            System.out.println("Not enough arguments:\n" +
                    "   Usage: ./client <PROTOCOL> [CONNECTIONS]");
            return;
        }
        Parameters params = new Parameters(
                /* delay = */ 100,
                /* retries = */ 10,
                /* serverHost = */ "localhost",
                /* serverPort = */ 10000,
                /* arraySize = */ 10,
                /* fixedThreadPoolSize = */ 100
        );
        Protocol protocol = new ClientServerProtocol();
        TasksProvider provider = new TasksProviderPRN(new Random(42), params.arraySize);
        Log log = new StandardLog();


        Client client = new ClientsFactory(protocol, provider, log)
                .createClient(type, params);

        try {
            client.start();
            client.join();
            System.out.println(client.getRunningTime());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
