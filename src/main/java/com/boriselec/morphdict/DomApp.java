package com.boriselec.morphdict;

import com.boriselec.morphdict.dom.data.Lemma;
import com.boriselec.morphdict.dom.edit.LemmaReader;
import com.boriselec.morphdict.dom.edit.LemmaTransformer;
import com.boriselec.morphdict.dom.out.LemmaWriter;
import com.boriselec.morphdict.dom.out.LemmaWriterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Optional;

public class DomApp {
    public static void main(String[] args) throws IOException, ParserConfigurationException, JAXBException {
        System.setProperty("line.separator", "\n");

        JAXBContext jaxbContext = JAXBContext.newInstance(Lemma.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        LemmaWriterFactory writerFactory = new LemmaWriterFactory(gson, marshaller);

        LemmaReader in = new LemmaReader(unmarshaller, "C:\\Users\\boris\\Downloads\\dict.opcorpora.xml\\dict.opcorpora.xml");
        LemmaWriter out = writerFactory.createXmlWriter("C:\\Users\\boris\\Downloads\\dict.opcorpora.xml\\dict.opcorpora.filtered");

        LemmaTransformer transformer = new LemmaTransformer();

        try {
            for (Lemma lemma : in) {
                Optional<Lemma> transformed = transformer.transform(lemma);
                transformed.ifPresent(out::write);
            }
        } finally {
            in.close();
            out.close();
        }
    }
}
