package core.client;

import util.Log;
import util.Parameters;
import wire.Protocol;

public class ClientsFactory {
    private volatile int idCounter = 0;
    private final Protocol protocol;
    private final TasksProvider tasksProvider;
    private final Log log;

    public ClientsFactory(Protocol protocol, TasksProvider tasksProvider, Log log) {
        this.protocol = protocol;
        this.tasksProvider = tasksProvider;
        this.log = log;
    }


    public Client createClient(ClientType type, Parameters parameters) {
        switch (type) {
            case SINGLE_TCP:
                return new OneConnectionTCPClient(log, idCounter++, protocol, tasksProvider, parameters);
            case MULTIPLE_TCP:
                return new ManyConnectionsTCPClient(log, idCounter++, protocol, tasksProvider, parameters);
            case MULTIPLE_UDP:
                return new ManyConnectionsUDPClient(log, idCounter++, protocol, tasksProvider, parameters);
            default:
                throw new IllegalArgumentException("Unknown client type: " + type);
        }
    }

    public enum ClientType {
        SINGLE_TCP,
        MULTIPLE_TCP,
        MULTIPLE_UDP;

        public static ClientType forInt(int id) {
            switch (id) {
                case 0: return SINGLE_TCP;
                case 1: return MULTIPLE_TCP;
                case 2: return MULTIPLE_UDP;
                default: throw new IllegalArgumentException("Unknown client id: " + id);
            }
        }
    }
}
