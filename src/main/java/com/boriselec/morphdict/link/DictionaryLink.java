package com.boriselec.morphdict.link;

import com.boriselec.morphdict.dom.out.LemmaWriter;

import java.nio.file.Path;

public abstract class DictionaryLink {
    private final String description;
    private final Path path;

    private Integer revision;

    public DictionaryLink(String description, Path path) {
        this.description = description;
        this.path = path;
    }

    public String getDescription() {
        return description;
    }

    public Path getPath() {
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
