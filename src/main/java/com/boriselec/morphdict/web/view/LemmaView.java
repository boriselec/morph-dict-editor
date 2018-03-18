package com.boriselec.morphdict.web.view;

import com.boriselec.morphdict.dom.data.Lemma;

import java.util.List;

public class LemmaView {
    private final MetaView meta;
    private final List<Lemma> lemmata;

    public LemmaView(List<Lemma> lemmata, int total) {
        this.lemmata = lemmata;
        this.meta = new MetaView(total);
    }
}
