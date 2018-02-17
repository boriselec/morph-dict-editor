package com.boriselec.morphdict.data;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.XMLEvent;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Contains xml events.
 * Implements reader interface, so can be used as writer source.
 */
public class EventContainer implements XMLEventReader {
    private final Iterator<XMLEvent> iterator;

    protected EventContainer(String name, XMLEvent startElement, XMLEventReader in) throws XMLStreamException {
        List<XMLEvent> events = new LinkedList<>();
        events.add(startElement);
        readUntilEndElement(events, name, in);
        iterator = events.iterator();
    }

    private void readUntilEndElement(List<XMLEvent> events, String name, XMLEventReader in) throws XMLStreamException {
        while (in.hasNext()) {
            XMLEvent event = in.nextEvent();
            if (event.isEndElement()) {
                EndElement endElement = event.asEndElement();
                if (name.equals(endElement.getName().getLocalPart())) {
                    events.add(event);
                    return;
                }
            }
            events.add(event);
        }
    }

    @Override
    public XMLEvent nextEvent() {
        return iterator.next();
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Object next() {
        return nextEvent();
    }

    @Override
    public XMLEvent peek() throws XMLStreamException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getElementText() throws XMLStreamException {
        throw new UnsupportedOperationException();
    }

    @Override
    public XMLEvent nextTag() throws XMLStreamException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getProperty(String name) throws IllegalArgumentException {
        return null;
    }

    @Override
    public void close() throws XMLStreamException {
    }
}
