package com.boriselec.morphdict.web;

import com.boriselec.morphdict.dom.data.Lemma;
import com.boriselec.morphdict.dom.edit.LemmaReader;
import com.boriselec.morphdict.dom.edit.LemmaTransformer;
import com.boriselec.morphdict.dom.in.FileLemmaReader;
import com.boriselec.morphdict.dom.out.CompositeLemmaWriter;
import com.boriselec.morphdict.dom.out.ConsoleProgressWriter;
import com.boriselec.morphdict.dom.out.LemmaWriter;
import com.boriselec.morphdict.link.FileDictRepository;
import com.boriselec.morphdict.load.DictLoader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.util.concurrent.locks.ReentrantLock;

@RestController("/admin")
@RequestMapping("/admin")
public class AdminController {
    private final DictLoader dictLoader;
    private final JAXBContext lemmaJaxbContext;
    private final LemmaWriter dbLemmaWriter;
    private final String inXmlPath;
    private final LemmaTransformer lemmaFilter;
    private final FileDictRepository fileDictRepository;

    private final ReentrantLock dbLock = new ReentrantLock();

    public AdminController(DictLoader dictLoader,
                           @Qualifier("database") LemmaWriter dbLemmaWriter,
                           JAXBContext lemmaJaxbContext,
                           @Value("${opencorpora.xml.path}") String inXmlPath,
                           LemmaTransformer lemmaFilter,
                           FileDictRepository fileDictRepository) {
        this.dictLoader = dictLoader;
        this.lemmaJaxbContext = lemmaJaxbContext;
        this.dbLemmaWriter = new CompositeLemmaWriter(new ConsoleProgressWriter(), dbLemmaWriter);
        this.inXmlPath = inXmlPath;
        this.lemmaFilter = lemmaFilter;
        this.fileDictRepository = fileDictRepository;
    }

    @RequestMapping(value = "/sync/dict/in", method = RequestMethod.POST)
    public void syncDict() {
        dictLoader.ensureLastVersion();
    }

    @RequestMapping(value = "/sync/db", method = RequestMethod.POST)
    public void syncDb() {
        withLock(dbLock, () -> {
            try (
                LemmaReader in = new FileLemmaReader(lemmaJaxbContext.createUnmarshaller(), inXmlPath)
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

    @RequestMapping(value = "/sync/dict/out", method = RequestMethod.POST)
    public void writeDictJson() {
        fileDictRepository.update();
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

}
