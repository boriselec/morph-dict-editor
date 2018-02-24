package com.boriselec.morphdict.stax.edit;

import com.boriselec.morphdict.stax.data.LemmaEvent;

import javax.xml.stream.*;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Transforms xml using filters
 */
public class XmlTransformer {
    private final XMLInputFactory xmlInputFactory;
    private final XMLOutputFactory xmlOutputFactory;
    private final LemmaHandler lemmaHandler;
    private final EventFilter disabledTagFilter;
    private final XmlAttributeFilter disabledAttrFilter;

    public XmlTransformer(XMLInputFactory xmlInputFactory,
                          XMLOutputFactory xmlOutputFactory,
                          LemmaHandler lemmaHandler,
                          EventFilter disabledTagFilter,
                          XmlAttributeFilter disabledAttrFilter) {
        this.xmlInputFactory = xmlInputFactory;
        this.xmlOutputFactory = xmlOutputFactory;
        this.lemmaHandler = lemmaHandler;
        this.disabledTagFilter = disabledTagFilter;
        this.disabledAttrFilter = disabledAttrFilter;
    }

    public void transform(InputStream inputStream, OutputStream outputStream) throws XMLStreamException {
        XMLEventReader in = xmlInputFactory.createXMLEventReader(inputStream);
        XMLEventWriter out = xmlOutputFactory.createXMLEventWriter(outputStream);

        while (in.hasNext()) {
            XMLEvent xmlEvent = in.nextEvent();
            if (isLemma(xmlEvent)) {
                LemmaEvent lemma = new LemmaEvent(xmlEvent.asStartElement(), in);
                switch (lemmaHandler.handle(lemma)) {
                    case SKIP:
                        break;
                    case CONTINUE:
                        for (XMLEvent event : lemma) {
                            flush(event, in, out);
                        }
                }
            } else {
                flush(xmlEvent, in, out);
            }
        }
    }

    private boolean isLemma(XMLEvent event) {
        return event.isStartElement() && "lemma".equals(event.asStartElement().getName().getLocalPart());
    }

    private void flush(XMLEvent xmlEvent, XMLEventReader in, XMLEventWriter out) throws XMLStreamException {
        if (disabledTagFilter.accept(xmlEvent)) {
            skipUntilEndElement(in, xmlEvent);
        } else {
            XMLEvent trimmedEvent = disabledAttrFilter.trim(xmlEvent);
            out.add(trimmedEvent);
        }
    }

    private void skipUntilEndElement(XMLEventReader in, XMLEvent xmlEvent) throws XMLStreamException {
        String name = xmlEvent.asStartElement().getName().getLocalPart();
        while (in.hasNext()) {
            XMLEvent nextEvent = in.nextEvent();
            if (nextEvent.isEndElement()) {
                EndElement endElement = nextEvent.asEndElement();
                if (name.equals(endElement.getName().getLocalPart())) {
                    return;
                }
            }
        }
    }
}
