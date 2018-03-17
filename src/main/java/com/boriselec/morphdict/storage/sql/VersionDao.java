package com.boriselec.morphdict.storage.sql;

import com.boriselec.morphdict.storage.VersionStorage;
import org.jdbi.v3.core.Jdbi;

import java.time.ZonedDateTime;

/**
 * Stores version in database
 */
public class VersionDao implements VersionStorage {
    private final Jdbi jdbi;

    public VersionDao(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    @Override
    public ZonedDateTime get() {
        return jdbi.withHandle(handle ->
            handle.createQuery("SELECT VALUE FROM DICTIONARY_VERSION")
                .mapTo(String.class)
                .findFirst())
            .map(s -> ZonedDateTime.parse(s, VERSION_FORMAT))
            .orElse(null);
    }

    @Override
    public void update(ZonedDateTime currentVersion) {
        String version = VERSION_FORMAT.format(currentVersion);
        jdbi.withHandle(handle -> {
            handle.execute("TRUNCATE TABLE DICTIONARY_VERSION");

            return handle.createUpdate("INSERT INTO DICTIONARY_VERSION(VALUE) VALUES (:version)")
                .bind("version", version)
                .execute();
        });
    }
}
