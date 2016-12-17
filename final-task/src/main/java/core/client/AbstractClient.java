package core.client;

import util.Log;
import util.Parameters;
import wire.Protocol;
import wire.WireMessages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

abstract class AbstractClient implements Client {
    private Thread runnerThread;
    private volatile long runningTime;
    private volatile IOException innerException = null;
    private volatile long packagesLost;

    protected final Log log;
    protected final int id;
    protected final String prefix;
    protected final Protocol protocol;
    protected final TasksProvider tasksProvider;
    protected final Parameters parameters;

    protected AbstractClient(Log log, int id, Protocol protocol, TasksProvider tasksProvider, Parameters parameters) {
        this.log = log;
        this.id = id;
        this.protocol = protocol;
        this.tasksProvider = tasksProvider;
        prefix = "[Client #" + id + "] ";
        this.parameters = parameters;
    }

    abstract void sendMessage(WireMessages.Numbers msg) throws IOException;
    abstract WireMessages.Numbers receiveMessage() throws IOException;

    protected void beforeClass() throws IOException { }

    protected void afterClass() throws IOException { }

    protected void beforeIteration() throws IOException { }

    protected void afterIteration() throws IOException { }

    @Override
    public void start() throws IOException {
        runnerThread = new Thread(() -> {
            try {
                execute();
            } catch (IOException e) {
                log.error(prefix + "error: " + e.getMessage());
                innerException = e;
            }
        });

        runnerThread.start();
    }

    @Override
    public boolean isFinished() {
        return !runnerThread.isAlive();
    }

    @Override
    public void join() throws InterruptedException {
        runnerThread.join();
    }

    @Override
    public long getRunningTime() throws IOException {
        if (runnerThread.isAlive()) {
            return -1;
        }

        if (innerException != null) {
            throw innerException;
        }
        return runningTime;
    }

    @Override
    public double getPackageLoss() throws IOException {
        if (runnerThread.isAlive()) {
            return -1;
        }

        if (innerException != null) {
            throw innerException;
        }
        return (double) packagesLost / parameters.retries;
    }

    private void execute() throws IOException {
        long begin = System.currentTimeMillis();
        long packagesLost = 0;
        beforeClass();
        try {
            for (int i = 0; i < parameters.retries; i++) {
                try {
                    log.trace(prefix + "retry #" + i);

                    beforeIteration();

                    log.trace(prefix + "sending message");
                    WireMessages.Numbers msg = tasksProvider.nextTask();
                    sendMessage(msg);

                    // Get expected result meanwhile
                    ArrayList<Integer> itemsList = new ArrayList<>(msg.getItemsList());
                    Collections.sort(itemsList);

                    log.trace(prefix + "waiting response");
                    WireMessages.Numbers result = receiveMessage();
                    if (result == null) {
                        log.trace(prefix + "message lost!");
                        packagesLost++;
                        continue;
                    }

                    if (!result.getItemsList().equals(itemsList)) {
                        log.error(prefix + "error! Returned result differs from the expected");
                        log.error("expected: " + itemsList);
                        log.error("got: " + result.getItemsList());
                    } else {
                        log.trace(prefix + "got correct result");
                    }
                    // End of iteration
                } catch (IOException e) {
                    log.error(prefix + e.getMessage());
                } finally {
                    afterIteration();
                }

                log.trace(prefix + "Sleeping...");
                try {
                    Thread.sleep(parameters.delay);
                } catch (InterruptedException ignored) { }
            }
            // End of retries-loop
        } finally {
            afterClass();
        }
        long end = System.currentTimeMillis();
        runningTime = end - begin;
        this.packagesLost = packagesLost;
    }


}
