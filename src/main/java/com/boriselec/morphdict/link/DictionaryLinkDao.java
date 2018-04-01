package com.boriselec.morphdict.link;

import org.jdbi.v3.core.Jdbi;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class DictionaryLinkDao {
    private final Jdbi jdbi;

    public DictionaryLinkDao(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    public void load(List<String> descriptions) {
        jdbi.withHandle(handle -> {
                for (String description : descriptions) {
                    Optional<Integer> current =
                        handle.createQuery("SELECT 1 FROM DICTIONARY_LINK WHERE DESCRIPTION = :description")
                            .bind("description", description)
                            .mapTo(Integer.class)
                            .findFirst();
                    if (!current.isPresent()) {
                        handle.createUpdate(
                            "INSERT INTO DICTIONARY_LINK (DESCRIPTION, REVISION) " +
                                "VALUES (:description, NULL)")
                            .bind("description", description)
                            .execute();
                    }
                }
                return null;
            }
        );
    }

    public Optional<Integer> getRevision(String description) {
        return jdbi.withHandle(handle ->
            handle.createQuery("SELECT REVISION FROM DICTIONARY_LINK WHERE DESCRIPTION = :description")
                .bind("description", description)
                .mapTo(Integer.class)
                .findFirst()
        );
    }

    public void updateRevision(String description, int revision) {
        jdbi.withHandle(handle ->
            handle.createUpdate("UPDATE DICTIONARY_LINK SET REVISION = :revision WHERE DESCRIPTION = :description")
                .bind("revision", revision)
                .bind("description", description)
                .execute()
        );
    }
}
