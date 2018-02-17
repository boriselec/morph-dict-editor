package com.boriselec.morphdict.data;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

/**
 * Lemma
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
public class Lemma extends EventContainer {
    public Lemma(XMLEvent startElement, XMLEventReader in) throws XMLStreamException {
        super("lemma", startElement, in);
    }
}
