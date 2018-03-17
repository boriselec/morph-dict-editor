package com.boriselec.morphdict.storage.sql;

import com.boriselec.morphdict.dom.data.Lemma;
import com.boriselec.morphdict.dom.out.LemmaWriter;
import com.google.gson.Gson;

import java.util.Optional;

/**
 * Loads lemmas into database
 */
public class DatabaseLemmaWriter implements LemmaWriter {
    private final LemmaDao dao;
    private final Gson gson;

    public DatabaseLemmaWriter(LemmaDao dao, Gson gson) {
        this.dao = dao;
        this.gson = gson;
    }

    @Override
    public void write(Lemma lemma) {
        Optional<Integer> currentRevision = dao.getRevision(lemma.id);

        if (currentRevision.isPresent()) {
            if (lemma.revision.equals(currentRevision.get())) {
                System.out.println("skipped");
                //skip
                return;
            } else {
                System.out.println("deleted");
                dao.delete(lemma.id);
            }
        }

        System.out.println("inserted");
        String json = gson.toJson(lemma);
        dao.insertFromCorpora(json, lemma);
    }

    @Override
    public void close() {
        //do nothing
    }
}
