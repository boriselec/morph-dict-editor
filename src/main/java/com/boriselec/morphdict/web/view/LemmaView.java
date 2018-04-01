package com.boriselec.morphdict.web.view;

import com.boriselec.morphdict.dom.data.Lemma;
import com.google.gson.annotations.Expose;

import java.util.List;

public class LemmaView {
    @Expose
    private final LemmaMetaView meta;
    @Expose
    private final List<Lemma> lemmata;

    public LemmaView(List<Lemma> lemmata, int total) {
        this.lemmata = lemmata;
        this.meta = new LemmaMetaView(total);
    }
}
