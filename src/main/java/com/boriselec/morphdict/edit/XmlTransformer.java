package com.boriselec.morphdict.edit;

import com.boriselec.morphdict.data.Lemma;

import javax.xml.stream.*;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Transforms xml using filters
 */
public class XmlTransformer {
    private final XMLInputFactory xmlInputFactory;
    private final XMLOutputFactory xmlOutputFactory;
    private final XMLEventFactory xmlEventFactory;

    public XmlTransformer(XMLInputFactory xmlInputFactory,
                          XMLOutputFactory xmlOutputFactory,
                          XMLEventFactory xmlEventFactory) {
        this.xmlInputFactory = xmlInputFactory;
        this.xmlOutputFactory = xmlOutputFactory;
        this.xmlEventFactory = xmlEventFactory;
    }

    public void transform(InputStream inputStream, OutputStream outputStream) throws XMLStreamException {
        XMLEventReader in = xmlInputFactory.createXMLEventReader(inputStream);
        XMLEventWriter out = xmlOutputFactory.createXMLEventWriter(outputStream);

        while (in.hasNext()) {
            XMLEvent xmlEvent = in.nextEvent();
            if (isLemma(xmlEvent)) {
                Lemma lemma = new Lemma(xmlEvent.asStartElement(), in);
                out.add(lemma);
            } else {
                flush(xmlEvent, out);
            }
        }
    }

    private boolean isLemma(XMLEvent event) {
        return event.isStartElement() && "lemma".equals(event.asStartElement().getName().getLocalPart());
    }

    private void flush(XMLEvent xmlEvent, XMLEventWriter out) throws XMLStreamException {
        out.add(xmlEvent);
    }
}
