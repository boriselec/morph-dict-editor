package com.boriselec.morphdict.storage.sql;

import com.boriselec.morphdict.dom.data.Lemma;
import com.boriselec.morphdict.dom.out.LemmaWriter;
import com.google.gson.Gson;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

import java.util.Optional;

/**
 * Loads lemmas into database
 */
public class DatabaseLemmaWriter implements LemmaWriter {
    private final Jdbi jdbi;
    private final Gson gson;

    public DatabaseLemmaWriter(Jdbi jdbi, Gson gson) {
        this.jdbi = jdbi;
        this.gson = gson;
    }

    @Override
    public void write(Lemma lemma) {
        jdbi.withHandle(handle -> {
            Optional<DictId> existing = selectExisting(handle, lemma);

            if (existing.isPresent()) {
                DictId dictId = existing.get();
                if (lemma.revision.equals(dictId.revision)) {
                    //skip
                    return null;
                } else {
                    deleteOutdated(handle, dictId.id);
                }
            }

            insert(handle, lemma);

            return null;
        });
    }

    private Optional<DictId> selectExisting(Handle handle, Lemma lemma) {
        return handle.select("SELECT ID, REVISION FROM LEMMA WHERE ID = :id")
            .bind("id", lemma.id)
            .map((rs, ctx) -> new DictId(rs.getInt("ID"), rs.getInt("REVISION")))
            .findFirst();
    }

    private void deleteOutdated(Handle handle, int id) {
        handle.createUpdate("DELETE FROM LEMMA WHERE ID = :id")
            .bind("id", id)
            .execute();
    }

    private void insert(Handle handle, Lemma lemma) {
        String json = gson.toJson(lemma);
        handle.createUpdate("INSERT INTO LEMMA (TEXT, JSON, ID, REVISION, STATE) " +
            "VALUES (:text, :json, :id, :revision, :state)")
            .bind("text", lemma.lemmaForm.text)
            .bind("json", json)
            .bind("id", lemma.id)
            .bind("revision", lemma.revision)
            .bind("state", lemma.state.getCode())
            .execute();
    }

    @Override
    public void close() {
        //do nothing
    }

    private static final class DictId {
        private Integer id;
        private Integer revision;

        private DictId(Integer id, Integer revision) {
            this.id = id;
            this.revision = revision;
        }
    }
}
