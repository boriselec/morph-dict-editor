package com.boriselec.morphdict.web.view;

import com.google.gson.annotations.Expose;

public class LinksMetaView {
    @Expose
    private final int revision;

    public LinksMetaView(int revision) {
        this.revision = revision;
    }
}
