package com.boriselec.morphdict.web.view;

import com.google.gson.annotations.Expose;

public class LemmaMetaView {
    @Expose
    private final int total;

    public LemmaMetaView(int total) {
        this.total = total;
    }
}
