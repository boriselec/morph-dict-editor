package com.boriselec.morphdict;

import com.boriselec.morphdict.dom.data.Lemma;
import com.boriselec.morphdict.dom.edit.*;
import com.boriselec.morphdict.dom.out.LemmaWriter;
import com.boriselec.morphdict.dom.out.LemmaWriterFactory;
import com.boriselec.morphdict.load.DictLoader;
import com.boriselec.morphdict.storage.VersionStorage;
import com.boriselec.morphdict.storage.sql.CompositeLemmaWriter;
import com.boriselec.morphdict.storage.sql.VersionDao;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jdbi.v3.core.Jdbi;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;

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

        Iterator<Lemma> predefined = Collections.<Lemma>emptyList().iterator();

        LemmaTransformer transformer = new ChainLemmaTransformer(
            new BlackListTextLemmaFilter("ёж"),
            new DigitLemmaFilter()
        );

        Jdbi jdbi = Jdbi.create("jdbc:h2:file:C:\\Users\\boris\\Downloads\\dict.opcorpora.xml\\db");
        VersionStorage versionStorage = new VersionDao(jdbi);

        String dict = "C:\\Users\\boris\\Downloads\\dict.opcorpora.xml\\dict.opcorpora.xml";
        DictLoader loader = new DictLoader(dict, versionStorage);
        loader.ensureLastVersion();

        try (
            FileLemmaReader in = new FileLemmaReader(unmarshaller, dict);
            LemmaWriter out = new CompositeLemmaWriter(
                writerFactory.createDatabaseWriter(jdbi, gson),
                writerFactory.createConsoleProgressWriter()
            )
        ) {
            for (Lemma lemma : in) {
                out.write(lemma);
            }
        }
    }
}
