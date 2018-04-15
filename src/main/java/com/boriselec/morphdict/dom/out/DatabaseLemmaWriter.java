package com.boriselec.morphdict.dom.out;

import com.boriselec.morphdict.dom.data.Lemma;
import com.boriselec.morphdict.storage.sql.LemmaDao;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Loads lemmas into database
 */
@Component("database")
public class DatabaseLemmaWriter implements LemmaWriter {
    private final LemmaDao dao;

    public DatabaseLemmaWriter(LemmaDao dao) {
        this.dao = dao;
    }

    @Override
    public void write(Lemma lemma) {
        Optional<Integer> currentRevision = dao.getRevision(lemma.id);

        if (currentRevision.isPresent()) {
            if (lemma.revision.equals(currentRevision.get())) {
                //skip
                return;
            } else {
                dao.remove(lemma.id);
            }
        }

        dao.insertFromCorpora(lemma);
    }

    @Override
    public void close() {
        //do nothing
    }
}
