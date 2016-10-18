package ru.spbau.mit.io;


public class LoggerFactory {
    private static final Logger defaultLogger = new StdOutLogger();

    public static Logger getDefaultLogger() {
        return defaultLogger;
    }
}
