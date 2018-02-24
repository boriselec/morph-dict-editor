package com.boriselec.morphdict.stax.edit;

import javax.xml.stream.EventFilter;
import javax.xml.stream.events.XMLEvent;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

/**
 * Checks for specific tags
 */
public class XmlTagFilter implements EventFilter {
    private final Set<String> tags;

    public XmlTagFilter(String... tags) {
        this.tags = new TreeSet<>(Arrays.asList(tags));
    }

    @Override
    public boolean accept(XMLEvent event) {
        return event.isStartElement() && tags.contains(event.asStartElement().getName().getLocalPart());
    }
}
