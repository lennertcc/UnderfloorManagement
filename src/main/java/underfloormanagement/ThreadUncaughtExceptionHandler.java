package underfloormanagement;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.logging.Logger;

public class ThreadUncaughtExceptionHandler implements UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Logger errorLog = Logger.getLogger("errorLog");
        errorLog.severe(String.format("UncaughtException in thread %s, message:\n%s", t.getName(), e.getMessage()));
    }
}
