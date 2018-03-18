package com.boriselec.morphdict.dom.out;

import com.boriselec.morphdict.dom.data.Lemma;
import com.boriselec.morphdict.storage.sql.LemmaDao;
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
                //skip
                return;
            } else {
                dao.delete(lemma.id);
            }
        }

        String json = gson.toJson(lemma);
        dao.insertFromCorpora(json, lemma);
    }

    @Override
    public void close() {
        //do nothing
    }
}