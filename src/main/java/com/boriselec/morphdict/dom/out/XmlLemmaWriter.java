package com.boriselec.morphdict.dom.out;

import com.boriselec.morphdict.dom.data.Lemma;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 * XML writer
 */
public class XmlLemmaWriter extends FileLemmaWriter {
    private final Marshaller marshaller;

    public XmlLemmaWriter(Marshaller marshaller, String path) {
        super(path);
        this.marshaller = marshaller;
        writeHeader();
    }

    private void writeHeader() {
        getWriter().println("<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\"?>");
        getWriter().println("<dictionary>");
        getWriter().println("<lemmata>");
    }

    @Override
    public void write(Lemma lemma) {
        try {
            marshaller.marshal(lemma, getWriter());
        } catch (JAXBException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        getWriter().println();
    }

    @Override
    protected void writeEnd() {
        getWriter().println("</lemmata>");
        getWriter().println("</dictionary>");
    }
}
