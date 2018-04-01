package com.boriselec.morphdict.web;

import com.boriselec.morphdict.dom.data.Lemma;
import com.boriselec.morphdict.dom.edit.LemmaReader;
import com.boriselec.morphdict.dom.edit.LemmaTransformer;
import com.boriselec.morphdict.dom.in.DatabaseLemmaReader;
import com.boriselec.morphdict.dom.in.FileLemmaReader;
import com.boriselec.morphdict.dom.out.*;
import com.boriselec.morphdict.load.DictLoader;
import com.boriselec.morphdict.storage.sql.LemmaDao;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.FileNotFoundException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

@RestController("/admin")
@RequestMapping("/admin")
public class AdminController {
    private final DictLoader dictLoader;
    private final JAXBContext lemmaJaxbContext;
    private final LemmaWriter dbLemmaWriter;
    private final String inXmlPath;
    private final LemmaTransformer lemmaFilter;
    private final Gson gson;
    private final String jsonPath;
    private final String xmlPath;
    private final LemmaDao lemmaDao;

    private final ReentrantLock dictLock = new ReentrantLock();
    private final ReentrantLock dbLock = new ReentrantLock();

    public AdminController(DictLoader dictLoader,
                           @Qualifier("database") LemmaWriter dbLemmaWriter,
                           JAXBContext lemmaJaxbContext,
                           @Value("${opencorpora.xml.path}") String inXmlPath,
                           LemmaTransformer lemmaFilter,
                           @Qualifier("internal") Gson gson,
                           @Value("${json.path}") String jsonPath,
                           @Value("${xml.path}") String xmlPath,
                           LemmaDao lemmaDao) {
        this.dictLoader = dictLoader;
        this.lemmaJaxbContext = lemmaJaxbContext;
        this.dbLemmaWriter = new CompositeLemmaWriter(new ConsoleProgressWriter(), dbLemmaWriter);
        this.inXmlPath = inXmlPath;
        this.lemmaFilter = lemmaFilter;
        this.gson = gson;
        this.jsonPath = jsonPath;
        this.xmlPath = xmlPath;
        this.lemmaDao = lemmaDao;
    }

    @RequestMapping(value = "/sync/dict", method = RequestMethod.POST)
    public void syncDict() {
        withLock(dictLock, dictLoader::ensureLastVersion);
    }

    @RequestMapping(value = "/sync/db", method = RequestMethod.POST)
    public void syncDb() {
        withLock(dbLock, () -> {
            try (
                LemmaReader in = new FileLemmaReader(lemmaJaxbContext.createUnmarshaller(), inXmlPath);
            ) {
                for (Lemma lemma : in) {
                    lemmaFilter.transform(lemma)
                        .ifPresent(dbLemmaWriter::write);
                }
            } catch (JAXBException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @RequestMapping(value = "/write/dict/json", method = RequestMethod.POST)
    public void writeDictJson() throws FileNotFoundException {
        writeDict(() -> new JsonLemmaWriter(gson, jsonPath));
    }

    @RequestMapping(value = "/write/dict/xml", method = RequestMethod.POST)
    public void writeDictXml() throws Exception {
        writeDict(() -> new XmlLemmaWriter(createMarshaller(), xmlPath));
    }

    private Marshaller createMarshaller() {
        try {
            Marshaller marshaller = lemmaJaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            return marshaller;
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    private void withLock(ReentrantLock lock, Runnable f) {
        if (lock.tryLock()) {
            try {
                f.run();
            } finally {
                lock.unlock();
            }
        } else {
            throw new IllegalStateException("In progress");
        }
    }

    private void writeDict(Supplier<LemmaWriter> lemmaWriter) {
        try (
            LemmaReader in = new DatabaseLemmaReader(lemmaDao);
            LemmaWriter out = new CompositeLemmaWriter(
                new ConsoleProgressWriter(),
                lemmaWriter.get());
        ) {
            for (Lemma lemma : in) {
                out.write(lemma);
            }
        }
    }
}
