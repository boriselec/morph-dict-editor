package com.boriselec.morphdict;

import com.boriselec.morphdict.dom.data.Lemma;
import com.boriselec.morphdict.dom.edit.*;
import com.boriselec.morphdict.dom.in.DatabaseLemmaReader;
import com.boriselec.morphdict.dom.out.CompositeLemmaWriter;
import com.boriselec.morphdict.dom.out.LemmaWriter;
import com.boriselec.morphdict.dom.out.LemmaWriterFactory;
import com.boriselec.morphdict.load.DictLoader;
import com.boriselec.morphdict.storage.VersionStorage;
import com.boriselec.morphdict.storage.sql.LemmaDao;
import com.boriselec.morphdict.storage.sql.VersionDao;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jdbi.v3.core.Jdbi;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.locks.ReentrantLock;

public class DomApp {
    public static void main(String[] args) throws Exception {
        System.setProperty("line.separator", "\n");

        JAXBContext jaxbContext = JAXBContext.newInstance(Lemma.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.excludeFieldsWithoutExposeAnnotation().create();

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

        Jdbi jdbi = Jdbi.create("jdbc:mysql://localhost:3306/dict", "root", "admin");
        VersionStorage versionStorage = new VersionDao(jdbi);

        LemmaDao lemmaDao = new LemmaDao(jdbi, gson);

        String dict = "C:\\Users\\boris\\Downloads\\dict.opcorpora.xml\\dict.opcorpora.xml";
        String zip = "C:\\Users\\boris\\Downloads\\dict.opcorpora.xml\\dict.opcorpora.xml.zip";
        DictLoader loader = new DictLoader(dict, zip, versionStorage, new ReentrantLock());
        loader.ensureLastVersion();

        try (
            LemmaReader in = new DatabaseLemmaReader(lemmaDao);
            LemmaWriter out = new CompositeLemmaWriter(
                writerFactory.createJsonWriter("C:\\Users\\boris\\Downloads\\dict.opcorpora.xml\\dict.opcorpora.filtered"),
                writerFactory.createConsoleProgressWriter()
            )
        ) {
            for (Lemma lemma : in) {
                out.write(lemma);
            }
        }
    }
}
