package com.boriselec.morphdict.link;

import com.boriselec.morphdict.dom.out.LemmaWriter;

public abstract class DictionaryLink {
    private final String description;
    private final String localPath;

    private Integer revision;

    public DictionaryLink(String description, String localPath) {
        this.description = description;
        this.localPath = localPath;
    }

    public String getDescription() {
        return description;
    }

    public String getLocalPath() {
        return localPath;
    }

    public Integer getRevision() {
        return revision;
    }

    public void setRevision(int revision) {
        this.revision = revision;
    }

    public abstract LemmaWriter getWriter();
}
