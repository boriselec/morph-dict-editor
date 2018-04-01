package com.boriselec.morphdict.dom.out;

import org.jdbi.v3.core.ConnectionException;
import org.jdbi.v3.core.HandleCallback;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dirty hack for connection issues
 * TODO batch update
 */
@Deprecated
public class RetryConnection {
    private static final Logger log = LoggerFactory.getLogger(ConsoleProgressWriter.class);

    private static final int TRIES_NUMBER = 50;
    private static final long SLEEP_MILLIS = 2000L;

    public static <R, X extends Exception> R retry(Jdbi jdbi, HandleCallback<R, X> callback) throws X {
        int tries = 0;
        while (tries < TRIES_NUMBER) {
            try {
                return jdbi.withHandle(callback);
            } catch (ConnectionException e) {
                tries++;
                log.error("connection issues");
                try {
                    Thread.sleep(SLEEP_MILLIS);
                } catch (InterruptedException e1) {
                    break;
                }
                log.error("trying again...");
            }
        }
        throw new ConnectionException(new RuntimeException("Cannot solve after " + tries + "tries"));
    }
}
