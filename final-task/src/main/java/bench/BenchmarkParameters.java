package bench;

import util.Parameters;

import static core.client.ClientsFactory.ClientType;
import static core.server.ServersFactory.ServerType;

public class BenchmarkParameters {
    // Possibly variable values
    private int delay;
    private int arraySize;
    private int clientsAmount;

    // Definitely constant values
    public final int retries;
    public final String serverHost;
    public final int serverPort;
    public final ServerType serverType;
    public final ClientType clientType;

    // Data for tabulation of variable
    public final VariableType type;
    public final int step;
    public final int finish;

    public enum VariableType {
        ARRAY_SIZE,
        CLIENTS_AMOUNT,
        DELAY
    }

    public BenchmarkParameters(
            Parameters parameters,
            VariableType type,
            int step,
            int finish,
            ServerType serverType,
            ClientType clientType,
            int clientsAmount
    ) {
        this.delay = parameters.delay;
        this.arraySize = parameters.arraySize;
        this.retries = parameters.retries;
        this.serverHost = parameters.serverHost;
        this.serverPort = parameters.serverPort;
        this.step = step;
        this.type = type;
        this.finish = finish;
        this.serverType = serverType;
        this.clientType = clientType;
        this.clientsAmount = clientsAmount;
    }

    public void makeStep() {
        switch (type) {
            case ARRAY_SIZE:
                arraySize += step;
                break;
            case CLIENTS_AMOUNT:
                clientsAmount += step;
                break;
            case DELAY:
                delay += step;
                break;
        }
    }

    public boolean isFinished() {
        switch (type) {
            case ARRAY_SIZE:
                return arraySize > finish;
            case CLIENTS_AMOUNT:
                return clientsAmount > finish;
            case DELAY:
                return delay > finish;
            default:
                throw new IllegalArgumentException("Unknown param type");
        }
    }

    public Parameters getCurParameters() {
        return new Parameters(
                delay,
                retries,
                serverHost,
                serverPort,
                arraySize,
                1000
        );
    }

    public int getClientsAmount() {
        return clientsAmount;
    }

    public int getDelay() {
        return delay;
    }

    public ServerType getServerType() {
        return serverType;
    }

    public ClientType getClientType() {
        return clientType;
    }

    public String getVariableName() {
        switch (type) {
            case ARRAY_SIZE:
                return "Array size";
            case CLIENTS_AMOUNT:
                return "Clients amount";
            case DELAY:
                return "Delay";
            default:
                throw new IllegalArgumentException("Unknown type: " + type);
        }
    }

    public int getVariableValue() {
        switch (type) {
            case ARRAY_SIZE:
                return arraySize;
            case CLIENTS_AMOUNT:
                return clientsAmount;
            case DELAY:
                return delay;
            default:
                throw new IllegalArgumentException("Unknown var type: " + type);
        }
    }

    public int getArraySize() {
        return arraySize;
    }

    public static class BenchmarkParametersBuilder {
        // Possibly variable values
        private int delay;
        private int arraySize;
        private int clientsAmount;

        private int retries;
        private String serverHost;
        private int serverPort;
        private ServerType serverType;
        private ClientType clientType;
        private VariableType type;
        private int step;
        private int finish;

        public BenchmarkParametersBuilder() { }

        public BenchmarkParametersBuilder setDelay(int value) {
            delay = value;
            return this;
        }

        public BenchmarkParametersBuilder setArraySize(int value) {
            arraySize = value;
            return this;
        }

        public BenchmarkParametersBuilder setClientsAmount(int value) {
            clientsAmount = value;
            return this;
        }

        public BenchmarkParametersBuilder setRetries(int value) {
            retries = value;
            return this;
        }

        public BenchmarkParametersBuilder setServerHost(String value) {
            serverHost = value;
            return this;
        }

        public BenchmarkParametersBuilder setServerPort(int value) {
            serverPort = value;
            return this;
        }

        public BenchmarkParametersBuilder setVariableType(VariableType type) {
            this.type = type;
            return this;
        }

        public BenchmarkParametersBuilder setStep(int value) {
            step = value;
            return this;
        }

        public BenchmarkParametersBuilder setFinishValue(int value) {
            finish = value;
            return this;
        }

        public BenchmarkParametersBuilder setClientType(ClientType value) {
            clientType = value;
            return this;
        }

        public BenchmarkParametersBuilder setServerType(ServerType value) {
            serverType = value;
            return this;
        }


        public BenchmarkParameters build() {
            return new BenchmarkParameters(
                    new Parameters(
                            delay,
                            retries,
                            serverHost,
                            serverPort,
                            arraySize,
                            1000
                    ),
                    type,
                    step,
                    finish,
                    serverType,
                    clientType,
                    clientsAmount
            );
        }
    }

}
