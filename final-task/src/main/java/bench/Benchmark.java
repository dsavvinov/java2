package bench;

import core.client.Client;
import core.client.ClientsFactory;
import core.client.TasksProviderPRN;
import core.server.InsertionSortExecutor;
import core.server.Server;
import core.server.ServersFactory;
import util.Log;
import util.Parameters;
import wire.ClientServerProtocol;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Benchmark {
    private final BenchmarkParameters params;
    private final Log log;
    private final ServersFactory serversFactory;
    private ClientsFactory clientsFactory;

    private final List<Integer> varValues = new ArrayList<>();
    private final List<Double> clientTimeValues = new ArrayList<>();
    private final List<Double> queryTimeValues = new ArrayList<>();
    private final List<Double> runningTimeValues = new ArrayList<>();
    private final List<Double> clientsPackageLoss = new ArrayList<>();
    private final String prefix = "[Benchmark]: ";

    public Benchmark(BenchmarkParameters params, Log log) {
        this.params = params;
        this.log = log;

        serversFactory = new ServersFactory(new ClientServerProtocol(), new InsertionSortExecutor(), log);
    }

    public void start() throws IOException {
        log.info(prefix + "starting");
        int totalWork = params.finish - params.getVariableValue();
        int stepsNeeded = totalWork / params.step + 1;
        double progressPerStep = 1.0 / stepsNeeded;

        double curProgress = 0.0;
        while(!params.isFinished()) {
            int percents = (int) (curProgress * 100.0);
            log.info(prefix + "finished " + percents + "%");
            makeIteration();
            curProgress += progressPerStep;
            params.makeStep();
        }
        log.info(prefix + "finished benchmark");

        double averagePackageLoss = clientsPackageLoss.stream().mapToDouble(it -> it).sum() / clientsPackageLoss.size();
        log.info(prefix + "package loss was " + averagePackageLoss + "%");
    }

    public List<Integer> getVarValues() {
        return varValues;
    }

    public List<Double> getClientTimeValues() {
        return clientTimeValues;
    }

    public List<Double> getQueryTimeValues() {
        return queryTimeValues;
    }

    public List<Double> getRunningTimeValues() {
        return runningTimeValues;
    }

    public String getVariableName() {
        switch (params.type) {
            case ARRAY_SIZE:
                return "N";
            case CLIENTS_AMOUNT:
                return "M";
            case DELAY:
                return "Delta";
            default:
                throw new IllegalArgumentException("Unknown type: " + params.type);
        }
    }

    public void saveStatistics(Path root) throws IOException {
        saveConditions(root.resolve("conditions.txt"));
        List<Double> serverOverhead = new ArrayList<>();
        for (int i = 0; i < varValues.size(); i++) {
            serverOverhead.add(clientTimeValues.get(i) - queryTimeValues.get(i));
        }

        dump(varValues, clientTimeValues, params.getVariableName(), "Client time", root.resolve("client-time.csv"));
        dump(varValues, queryTimeValues, params.getVariableName(), "Query time", root.resolve("query-time.csv"));
        dump(varValues, runningTimeValues, params.getVariableName(), "Running time", root.resolve("running-time.csv"));
        dump(varValues, serverOverhead, params.getVariableName(), "Server overhead", root.resolve("server-overhead.csv"));
    }

    private void saveConditions(Path target) throws IOException {
        FileWriter fileWriter = new FileWriter(target.toFile());
        try (BufferedWriter out = new BufferedWriter(fileWriter)) {

            String variableDescription = "was iterating from = " +
                    varValues.get(0) +
                    " to = " +
                    varValues.get(varValues.size() - 1) +
                    " with step = " +
                    params.step;

            if (params.type == BenchmarkParameters.VariableType.ARRAY_SIZE) {
                out.write("Array size " + variableDescription);
            } else {
                out.write("Array size = " + params.getArraySize());
            }

            out.newLine();

            if (params.type == BenchmarkParameters.VariableType.CLIENTS_AMOUNT) {
                out.write("Clients amount " + variableDescription);
            } else {
                out.write("Clients amount = " + params.getClientsAmount());
            }

            out.newLine();

            if (params.type == BenchmarkParameters.VariableType.DELAY) {
                out.write("Delay " + variableDescription);
            } else {
                out.write("Delay = " + params.getDelay());
            }

            out.newLine();
            out.write("Amount of retries = " + params.retries);
            out.newLine();
            out.write("Server type = " + params.serverType.toString());
            out.newLine();
            out.write("Client type = " + params.clientType.toString());
            out.newLine();
        }
    }

    private void dump(List<Integer> xs, List<Double> ys, String xName, String yName, Path target) throws IOException {
        FileWriter fileWriter = new FileWriter(target.toFile());
        try (BufferedWriter out = new BufferedWriter(fileWriter)) {

            out.write(xName + ", " + yName);
            out.newLine();

            for (int i = 0; i < xs.size(); i++) {
                Integer x = xs.get(i);
                Double y = ys.get(i);
                out.write(x + ", " + y);
                out.newLine();
            }
        }
    }

    private void makeIteration() throws IOException {
        Parameters curParameters = params.getCurParameters();
        Server server = serversFactory
                .createServer(params.getServerType(), curParameters);
        server.start();
        log.trace(prefix + "started server");

        clientsFactory = new ClientsFactory(
                new ClientServerProtocol(),
                new TasksProviderPRN(new Random(42), curParameters.arraySize),
                log
        );

        List<Client> clients = new ArrayList<>();
        for (int i = 0; i < params.getClientsAmount(); i++) {
            Client client = clientsFactory.createClient(params.getClientType(), curParameters);
            clients.add(client);
            client.start();
            log.trace(prefix + "started " + (i + 1) + " out of " + params.getClientsAmount() + " clients");
        }

        for (Client client : clients) {
            while (!client.isFinished()) {
                try {
                    client.join();
                } catch (InterruptedException ignored) { }
            }
        }

        log.trace(prefix + "clients finished, shutting down server");
        server.shutdown();

        log.trace(prefix + "server shut down, collecting statistics");
        collectStatistics(server, clients);
    }

    private void collectStatistics(Server server, List<Client> clients) throws IOException {
        int variableValue = params.getVariableValue();
        varValues.add(variableValue);

        clientTimeValues.add(server.getAverageClientTime());
        queryTimeValues.add(server.getAverageQueryTime());

        double averageRunningTime = 0;
        double averagePackageLoss = 0;
        for (Client client : clients) {
            averageRunningTime += (double) client.getRunningTime() / clients.size();
            averagePackageLoss += client.getPackageLoss() / clients.size();
        }
        runningTimeValues.add(averageRunningTime);
        clientsPackageLoss.add(averagePackageLoss);
    }
}
