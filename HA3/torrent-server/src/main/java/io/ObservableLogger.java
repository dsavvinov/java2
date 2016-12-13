package io;

import java.util.Observable;

public class ObservableLogger extends Observable implements Logger {
    private Logger standardLogger = StandardLogger.getInstance();
    
    @Override
    public void info(String message) {
        standardLogger.info(message);
        setChanged();
        notifyObservers(message);
    }

    @Override
    public void error(String message) {
        standardLogger.error(message);
        setChanged();
        notifyObservers(message);
    }

    @Override
    public void trace(String message) {
        standardLogger.trace(message);
        setChanged();
        notifyObservers(message);
    }

    @Override
    public void debug(String message) {
        standardLogger.debug(message);
        setChanged();
        notifyObservers(message);
    }
}
