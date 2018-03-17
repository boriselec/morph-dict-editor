package com.boriselec.morphdict.dom.out;

import com.boriselec.morphdict.dom.data.Lemma;

public class CompositeLemmaWriter implements LemmaWriter {
    private final LemmaWriter[] writers;

    public CompositeLemmaWriter(LemmaWriter... writers) {
        this.writers = writers;
    }

    @Override
    public void write(Lemma lemma) {
        for (LemmaWriter writer : writers) {
            writer.write(lemma);
        }
    }

    @Override
    public void close() {
        for (LemmaWriter writer : writers) {
            writer.close();
        }
    }
}
