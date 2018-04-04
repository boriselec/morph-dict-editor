package com.boriselec.morphdict.dom.out;

import com.boriselec.morphdict.storage.sql.LemmaDao;
import com.google.gson.Gson;
import org.slf4j.LoggerFactory;

import javax.xml.bind.Marshaller;
import java.io.FileNotFoundException;

public class LemmaWriterFactory {
    private final Gson gson;
    private final Marshaller marshaller;

    public LemmaWriterFactory(Gson gson, Marshaller marshaller) {
        this.gson = gson;
        this.marshaller = marshaller;
    }

    public LemmaWriter createJsonWriter(String path) throws FileNotFoundException {
        return new JsonLemmaWriter(gson, path);
    }

    public LemmaWriter createXmlWriter(String path) throws FileNotFoundException {
        return new XmlLemmaWriter(marshaller, path);
    }

    public LemmaWriter createDatabaseWriter(LemmaDao dao) {
        return new DatabaseLemmaWriter(dao);
    }

    public LemmaWriter createConsoleProgressWriter() {
        return new ConsoleProgressWriter(LoggerFactory.getLogger(ConsoleProgressWriter.class));
    }
}
