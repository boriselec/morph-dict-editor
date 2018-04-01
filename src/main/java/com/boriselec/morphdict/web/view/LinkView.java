package com.boriselec.morphdict.web.view;

import com.google.gson.annotations.Expose;

public class LinkView {
    @Expose
    private final String name;
    @Expose
    private final String url;
    @Expose
    private final Integer revision;

    public LinkView(String name, String url, Integer revision) {
        this.name = name;
        this.url = url;
        this.revision = revision;
    }
}
