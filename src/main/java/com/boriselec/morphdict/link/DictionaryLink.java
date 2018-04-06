package com.boriselec.morphdict.link;

import com.boriselec.morphdict.dom.out.LemmaWriter;

public abstract class DictionaryLink {
    private final String description;
    private final String path;

    private Integer revision;

    public DictionaryLink(String description, String path) {
        this.description = description;
        this.path = path;
    }

    public String getDescription() {
        return description;
    }

    public String getPath() {
        return path;
    }

    public Integer getRevision() {
        return revision;
    }

    public void setRevision(int revision) {
        this.revision = revision;
    }

    public abstract LemmaWriter getWriter();
}
