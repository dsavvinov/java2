package util;

import java.util.Observable;

public class ObservableLog extends Observable implements Log {
    private final boolean verbose;

    public ObservableLog(boolean verbose) {
        this.verbose = verbose;
    }

    @Override
    public void info(String message) {
        setChanged();
        notifyObservers(message + "\n");
        System.out.println(message);
    }

    @Override
    public void trace(String message) {
        if (verbose) {
            setChanged();
            notifyObservers(message + "\n");
            System.out.println(message);
        }
    }

    @Override
    public void error(String message) {
        setChanged();
        notifyObservers(message + "\n");
        System.err.println(message);
    }
}
