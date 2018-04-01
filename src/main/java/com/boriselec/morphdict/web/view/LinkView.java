package com.boriselec.morphdict.web.view;

import com.boriselec.morphdict.link.DictionaryLink;
import com.google.gson.annotations.Expose;

public class LinkView {
    @Expose
    private final String name;
    @Expose
    private final String url;
    @Expose
    private final Integer revision;

    public LinkView(DictionaryLink link) {
        this.name = link.getDescription();
        this.url = link.getLocalPath();
        this.revision = link.getRevision();
    }
}
