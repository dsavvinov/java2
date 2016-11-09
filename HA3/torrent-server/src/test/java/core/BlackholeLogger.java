package core;

import io.Logger;

/**
 * Logger that doesn't log anything and throws on any attempt to log error
 */
public class BlackholeLogger implements Logger {
    @Override
    public void info(String message) {

    }

    @Override
    public void error(String message) {
        throw new RuntimeException(message);
    }

    @Override
    public void trace(String message) {

    }

    @Override
    public void debug(String message) {

    }
}
