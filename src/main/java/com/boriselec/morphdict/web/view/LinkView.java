package com.boriselec.morphdict.web.view;

import com.google.gson.annotations.Expose;

public class LinkView {
    @Expose
    private final String name;
    @Expose
    private final String url;

    public LinkView(String name, String url) {
        this.name = name;
        this.url = url;
    }
}
