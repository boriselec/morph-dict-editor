package com.boriselec.morphdict.web.view;

import com.boriselec.morphdict.link.DictionaryLink;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class LinksView {
    @Expose
    private final LinksMetaView meta;
    @Expose
    private final List<LinkView> link = new ArrayList<>();

    public LinksView(List<DictionaryLink> link, int revision) {
        this.meta = new LinksMetaView(revision);
        for (DictionaryLink dictionaryLink : link) {
            this.link.add(new LinkView(dictionaryLink));
        }
    }
}
