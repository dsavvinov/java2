package core;

import bench.Benchmark;
import bench.BenchmarkParameters;
import util.StandardLog;

import java.io.IOException;
import java.nio.file.Paths;

import static bench.BenchmarkParameters.BenchmarkParametersBuilder;
import static bench.BenchmarkParameters.VariableType;
import static core.client.ClientsFactory.ClientType;
import static core.server.ServersFactory.ServerType;

public class BenchesCLI {
    public static void main(String[] args) {
        ClientType clientType = ClientType.MULTIPLE_TCP;
        ServerType serverType = ServerType.NIO_TCP;

        BenchmarkParameters benchParams = new BenchmarkParametersBuilder()
                .setArraySize(1000)
                .setClientsAmount(10)
                .setClientType(clientType)
                .setServerType(serverType)
                .setDelay(100)
                .setRetries(10)
                .setServerHost("localhost")
                .setServerPort(10000)
                .setVariableType(VariableType.ARRAY_SIZE)
                .setStep(100)
                .setFinishValue(1500)
                .build();

        Benchmark benchmark = new Benchmark(benchParams, new StandardLog());
        try {
            benchmark.start();
            System.out.println("Iteration values: \n" + benchmark.getVarValues() + "\n");
            System.out.println("Client time: \n" + benchmark.getClientTimeValues() + "\n");
            System.out.println("Query time: \n" + benchmark.getQueryTimeValues() + "\n");
            System.out.println("Running time: \n" + benchmark.getRunningTimeValues() + "\n");

            benchmark.saveStatistics(Paths.get(System.getProperty("user.dir")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
