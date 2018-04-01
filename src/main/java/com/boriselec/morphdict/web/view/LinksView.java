package com.boriselec.morphdict.web.view;

import com.google.gson.annotations.Expose;

import java.util.List;

public class LinksView {
    @Expose
    private final LinksMetaView meta;
    @Expose
    private final List<LinkView> link;

    public LinksView(List<LinkView> link, int revision) {
        this.meta = new LinksMetaView(revision);
        this.link = link;
    }
}
