package com.boriselec.morphdict.dom.in;

import com.boriselec.morphdict.dom.data.Lemma;
import com.boriselec.morphdict.dom.edit.LemmaReader;
import com.boriselec.morphdict.storage.sql.LemmaDao;

import java.util.Iterator;
import java.util.List;

public class DatabaseLemmaReader implements LemmaReader {
    private static final int BATCH_SIZE = 1000;
    private final LemmaDao dao;
    private Iterator<Lemma> currentBatch;
    private int cursor = 0;

    public DatabaseLemmaReader(LemmaDao dao) {
        this.dao = dao;
    }

    @Override
    public boolean hasNext() {
        ensureBatch();
        return currentBatch.hasNext();
    }

    @Override
    public Lemma next() {
        ensureBatch();
        return currentBatch.next();
    }

    private void ensureBatch() {
        if (currentBatch == null || !currentBatch.hasNext()) {
            currentBatch = fetch();
        }
    }

    private Iterator<Lemma> fetch() {
        List<Lemma> lemmata = dao.get(cursor, BATCH_SIZE);
        cursor += BATCH_SIZE;
        return lemmata.iterator();
    }

    @Override
    public Iterator<Lemma> iterator() {
        return this;
    }

    @Override
    public void close() {
    }
}
