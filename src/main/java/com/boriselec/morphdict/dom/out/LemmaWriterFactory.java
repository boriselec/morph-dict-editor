package com.boriselec.morphdict.dom.out;

import com.boriselec.morphdict.dom.data.Lemma;
import com.boriselec.morphdict.storage.sql.DatabaseLemmaWriter;
import com.google.gson.Gson;
import org.jdbi.v3.core.Jdbi;

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

    public LemmaWriter createDatabaseWriter(Jdbi jdbi, Gson gson) {
        return new DatabaseLemmaWriter(jdbi, gson);
    }

    public LemmaWriter createConsoleProgressWriter() {
        return new LemmaWriter() {
            private int count = 0;
            @Override
            public void write(Lemma lemma) {
                count++;
                if (count % 1000 == 0) {
                    System.out.println(count);
                }
            }

            @Override
            public void close() {
            }
        };
    }
}
