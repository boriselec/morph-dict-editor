package com.boriselec.morphdict.storage.sql;

import com.boriselec.morphdict.dom.data.Lemma;
import com.boriselec.morphdict.dom.data.LemmaState;
import com.boriselec.morphdict.dom.out.RetryConnection;
import com.google.gson.Gson;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

@Component
public class LemmaDao {
    private static final int NO_REVISION = -1;
    private final Jdbi jdbi;
    private final Gson gson;

    public LemmaDao(Jdbi jdbi, @Qualifier("internal") Gson gson) {
        this.jdbi = jdbi;
        this.gson = gson;
    }

    public List<Lemma> get(int offset, int limit) {
        return jdbi.withHandle(handle ->
            handle.createQuery("SELECT * FROM LEMMA ORDER BY ID LIMIT :limit OFFSET :offset")
                .bind("limit", limit)
                .bind("offset", offset)
                .map((rs, ctx) -> deserialize(rs))
                .list()
        );
    }

    public List<Lemma> search(String text, int offset, int limit) {
        return jdbi.withHandle(handle ->
            handle.createQuery(
                "SELECT * FROM LEMMA " +
                    "WHERE TEXT LIKE CONCAT('%', :text, '%') " +
                    "ORDER BY ID LIMIT :limit OFFSET :offset")
                .bind("text", text)
                .bind("limit", limit)
                .bind("offset", offset)
                .map((rs, ctx) -> deserialize(rs))
                .list()
        );
    }

    public void delete(int id) {
        jdbi.withHandle(handle ->
            handle.createUpdate("UPDATE LEMMA SET STATE = :state WHERE ID = :id")
                .bind("id", id)
                .bind("state", LemmaState.DELETED.getCode())
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

    public void insertFromCorpora(Lemma lemma) {
        String json = gson.toJson(lemma);
        insert(json,
            lemma.lemmaForm.text,
            requireNonNull(lemma.id),
            requireNonNull(lemma.revision),
            requireNonNull(lemma.state)
        );
    }

    public void insertNew(String json) {
        Lemma lemma = gson.fromJson(json, Lemma.class);
        insert(json,
            lemma.lemmaForm.text,
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

    public int totalSearch(String text) {
        return jdbi.withHandle(handle ->
            handle.createQuery("SELECT COUNT(*) FROM LEMMA WHERE TEXT LIKE CONCAT('%', :text, '%')")
                .bind("text", text)
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

    private Lemma deserialize(ResultSet rs) throws SQLException {
        Lemma lemma = gson.fromJson(rs.getString("JSON"), Lemma.class);
        lemma.id = rs.getInt("ID");
        lemma.state = LemmaState.fromCode(rs.getInt("STATE"));
        lemma.revision = rs.getInt("REVISION");
        return lemma;
    }
}
