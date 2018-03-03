package com.boriselec.morphdict.dom.out;

import com.boriselec.morphdict.dom.data.Lemma;

public interface LemmaWriter extends AutoCloseable {
    void write(Lemma lemma);
    void close();
}
