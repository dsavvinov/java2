package io;

public class StandardLogger implements Logger {
    private static boolean TRACE_ENABLED = true;
    private static boolean DEBUG_ENABLED = true;

    public static StandardLogger getInstance() {
        // StandardLogger is stateless, so it's OK to return new instance every time
        return new StandardLogger();
    }
    public static void disableTrace() { TRACE_ENABLED = false; }
    public static void disableDebug() { DEBUG_ENABLED = false; }

    @Override
    public void info(String message) {
        System.out.println(message);
    }

    @Override
    public void error(String message) {
        System.err.println(message);
    }

    @Override
    public void trace(String message) {
        if (TRACE_ENABLED) {
            System.out.println(message);
        }
    }

    @Override
    public void debug(String message) {
        if (DEBUG_ENABLED) {
            System.out.println(message);
        }
    }

    private StandardLogger() {}
}
