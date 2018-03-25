package com.boriselec.morphdict.web.view;

import com.google.gson.annotations.Expose;

public class MetaView {
    @Expose
    private final int total;

    public MetaView(int total) {
        this.total = total;
    }
}
