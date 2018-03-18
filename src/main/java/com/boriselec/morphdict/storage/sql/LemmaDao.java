package com.boriselec.morphdict.storage.sql;

import com.boriselec.morphdict.dom.data.Lemma;
import com.boriselec.morphdict.dom.data.LemmaState;
import com.boriselec.morphdict.dom.out.RetryConnection;
import org.jdbi.v3.core.Jdbi;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

import static java.util.Objects.requireNonNull;

@Component
public class LemmaDao {
    private static final int NO_REVISION = -1;
    private final Jdbi jdbi;

    public LemmaDao(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    public List<Lemma> get(int offset, int limit, BiFunction<Integer, String, Lemma> deserializer) {
        return jdbi.withHandle(handle ->
            handle.createQuery("SELECT ID, JSON FROM LEMMA ORDER BY ID LIMIT :limit OFFSET :offset")
                .bind("limit", limit)
                .bind("offset", offset)
                .map((rs, ctx) -> deserializer.apply(rs.getInt("ID"), rs.getString("JSON")))
                .list()
        );
    }

    public void delete(int id) {
        jdbi.withHandle(handle ->
            handle.createUpdate("DELETE FROM LEMMA WHERE ID = :id")
                .bind("id", id)
                .execute()
        );
    }

    public Optional<Integer> getRevision(int id) {
        return RetryConnection.retry(jdbi, handle ->
            handle.select("SELECT REVISION FROM LEMMA WHERE ID = :id")
                .bind("id", id)
                .mapTo(Integer.class)
                .findFirst()
        );
    }

    public void insertFromCorpora(String json, Lemma lemma) {
        insert(json,
            lemma.lemmaForm.text,
            requireNonNull(lemma.id),
            requireNonNull(lemma.revision),
            LemmaState.OPENCORPORA
        );
    }

    public void insertNew(String json, String text) {
        insert(json,
            text,
            generateId(),
            NO_REVISION,
            LemmaState.MANUAL
        );
    }

    public int total() {
        return jdbi.withHandle(handle ->
            handle.createQuery("SELECT COUNT(*) FROM LEMMA")
                .mapTo(Integer.class)
                .findOnly()
        );
    }

    private void insert(String json, String text, int id, int revision, LemmaState state) {
        RetryConnection.retry(jdbi, handle ->
            handle.createUpdate("INSERT INTO LEMMA (TEXT, JSON, ID, REVISION, STATE) " +
                "VALUES (:text, :json, :id, :revision, :state)")
                .bind("text", text)
                .bind("json", json)
                .bind("id", id)
                .bind("revision", revision)
                .bind("state", state.getCode())
                .execute()
        );
    }

    private int generateId() {
        return jdbi.withHandle(handle ->
            handle.createQuery("SELECT NEXT VALUE FOR LEMMA_ID")
                .mapTo(Integer.class)
                .findOnly()
        );
    }
}
