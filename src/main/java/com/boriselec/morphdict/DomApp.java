package com.boriselec.morphdict;

import com.boriselec.morphdict.dom.data.Lemma;
import com.boriselec.morphdict.dom.edit.*;
import com.boriselec.morphdict.dom.out.LemmaWriter;
import com.boriselec.morphdict.dom.out.LemmaWriterFactory;
import com.boriselec.morphdict.load.DictLoader;
import com.boriselec.morphdict.storage.VersionStorage;
import com.boriselec.morphdict.storage.file.FileVersionStorage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
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

        Lemma newLemma = new Lemma.Builder()
            .addLemma("лайтово", "ADVB")
            .addForm("лайтово")
            .build();

        Iterator<Lemma> predefined = Arrays.asList(newLemma).iterator();

        LemmaTransformer transformer = new ChainLemmaTransformer(
            new BlackListTextLemmaFilter("ёж"),
            new DigitLemmaFilter()
        );
        VersionStorage versionStorage = new FileVersionStorage("C:\\Users\\boris\\Downloads\\dict.opcorpora.xml\\.dictversion");

        String dict = "C:\\Users\\boris\\Downloads\\dict.opcorpora.xml\\dict.opcorpora.xml";
        DictLoader loader = new DictLoader(dict, versionStorage);
        loader.ensureLastVersion();

        try (
            LemmaReader in = new LemmaReader(unmarshaller, dict, predefined);
            LemmaWriter out = writerFactory.createJsonWriter("C:\\Users\\boris\\Downloads\\dict.opcorpora.xml\\dict.opcorpora.filtered");
        ) {
            for (Lemma lemma : in) {
                Optional<Lemma> transformed = transformer.transform(lemma);
                transformed.ifPresent(out::write);
            }
        }
    }
}
