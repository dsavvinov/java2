package core.client;

import java.io.IOException;

public interface Client {
    void start() throws IOException;
    boolean isFinished();
    void join() throws InterruptedException;
    long getRunningTime() throws IOException;
    double getPackageLoss() throws IOException;
}
