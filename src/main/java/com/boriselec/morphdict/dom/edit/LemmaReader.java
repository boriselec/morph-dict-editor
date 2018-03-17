package com.boriselec.morphdict.dom.edit;

import com.boriselec.morphdict.dom.data.Lemma;

import java.util.Iterator;

public interface LemmaReader extends Iterator<Lemma>, Iterable<Lemma>, AutoCloseable {
}
