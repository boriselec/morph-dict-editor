package com.boriselec.morphdict.stax.data;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

/**
 * LemmaEvent
 *
 * <pre>
 * {@code
 *
 *     <lemma id="1" rev="402007">
 *         <l t="абажур"><g v="NOUN"/><g v="inan"/><g v="masc"/></l>
 *         <f t="абажур"><g v="sing"/><g v="nomn"/></f>
 *         <f t="абажура"><g v="sing"/><g v="gent"/></f>
 *         ...
 *     </lemma>
 * }
 * </pre>
 */
public class LemmaEvent extends EventContainer {
    private String text;

    public LemmaEvent(XMLEvent startElement, XMLEventReader in) throws XMLStreamException {
        read("lemma", startElement, in, this::extractCanonical);
        assert text != null;
    }

    private void extractCanonical(XMLEvent event) {
        if (event.isStartElement() && "l".equals(event.asStartElement().getName().getLocalPart())) {
            assert text == null;
            this.text = event.asStartElement().getAttributeByName(QName.valueOf("t")).getValue();
        }
    }

    public String getText() {
        return text;
    }
}
