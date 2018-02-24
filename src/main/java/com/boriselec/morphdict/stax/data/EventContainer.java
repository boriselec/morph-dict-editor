package com.boriselec.morphdict.stax.data;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.XMLEvent;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Contains xml events.
 */
public class EventContainer implements Iterable<XMLEvent> {
    private Iterator<XMLEvent> iterator;

    protected void read(String name,
                        XMLEvent startElement,
                        XMLEventReader in,
                        Consumer<XMLEvent>... handlers) throws XMLStreamException {
        List<XMLEvent> events = new LinkedList<>();
        addEvent(events, startElement, handlers);
        readUntilEndElement(events, name, in, handlers);
        iterator = events.iterator();
    }

    private void readUntilEndElement(List<XMLEvent> events,
                                     String name,
                                     XMLEventReader in,
                                     Consumer<XMLEvent>... handlers) throws XMLStreamException {
        while (in.hasNext()) {
            XMLEvent event = in.nextEvent();
            if (event.isEndElement()) {
                EndElement endElement = event.asEndElement();
                if (name.equals(endElement.getName().getLocalPart())) {
                    addEvent(events, event, handlers);
                    return;
                }
            }
            addEvent(events, event, handlers);
        }
    }

    private void addEvent(List<XMLEvent> events, XMLEvent newEvent, Consumer<XMLEvent>... handlers) {
        for (Consumer<XMLEvent> handler : handlers) {
            handler.accept(newEvent);
        }
        events.add(newEvent);
    }

    @Override
    public Iterator<XMLEvent> iterator() {
        return iterator;
    }
}
