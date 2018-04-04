package com.boriselec.morphdict.storage.sql;

import com.boriselec.morphdict.storage.VersionStorage;
import org.jdbi.v3.core.Jdbi;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

/**
 * Stores version in database
 */
@Component
public class VersionDao implements VersionStorage {
    private final Jdbi jdbi;

    public VersionDao(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    @Override
    public ZonedDateTime get(VersionType type) {
        return jdbi.withHandle(handle ->
            handle.createQuery("SELECT VALUE FROM DICTIONARY_VERSION WHERE TYPE = :type")
                .bind("type", type)
                .mapTo(String.class)
                .findFirst())
            .map(s -> ZonedDateTime.parse(s, VERSION_FORMAT))
            .orElse(null);
    }

    @Override
    public void update(VersionType type, ZonedDateTime currentVersion) {
        String version = VERSION_FORMAT.format(currentVersion);
        jdbi.withHandle(handle -> {
            handle.createUpdate("DELETE FROM DICTIONARY_VERSION WHERE TYPE = :type")
                .bind("type", type)
                .execute();

            return handle.createUpdate("INSERT INTO DICTIONARY_VERSION(TYPE, VALUE) VALUES (:type, :version)")
                .bind("type", type)
                .bind("version", version)
                .execute();
        });
    }
}
