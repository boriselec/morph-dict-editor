package com.boriselec.morphdict.storage.sql;

import com.boriselec.morphdict.dom.data.Lemma;
import org.jdbi.v3.core.Jdbi;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@Component
public class LemmaDao {
    private final Jdbi jdbi;

    public LemmaDao(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    public List<Lemma> get(int from, int to, Function<String, Lemma> deserializer) {
        return jdbi.withHandle(handle ->
            handle.createQuery("SELECT JSON FROM LEMMA WHERE ROWNUM BETWEEN :from AND :to")
                .bind("from", from)
                .bind("to", to)
                .map((rs, ctx) -> deserializer.apply(rs.getString("JSON")))
                .list()
        );
    }
}
