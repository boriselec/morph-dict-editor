package com.boriselec.morphdict.stax.edit;

import com.google.common.collect.Iterators;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * trip specific xml attributes inside tags
 */
public class XmlAttributeFilter {
    private final XMLEventFactory eventFactory;
    private final Set<String> disabledAttributes;

    public XmlAttributeFilter(XMLEventFactory eventFactory, String... disabledAttributes) {
        this.eventFactory = eventFactory;
        this.disabledAttributes = new TreeSet<>(Arrays.asList(disabledAttributes));
    }

    @SuppressWarnings("unchecked")
    public XMLEvent trim(XMLEvent event) {
        if (event.isStartElement()) {
            StartElement startElement = event.asStartElement();
            Iterator<Attribute> attributes = startElement.getAttributes();
            Iterator<Attribute> filtered = Iterators.filter(attributes,
                attr -> !disabledAttributes.contains(attr.getName().getLocalPart()));
            return eventFactory.createStartElement(startElement.getName(), filtered, null);
        } else {
            return event;
        }
    }
}
