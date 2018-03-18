package com.boriselec.morphdict.web;

import com.boriselec.morphdict.dom.data.Lemma;
import com.boriselec.morphdict.dom.edit.LemmaReader;
import com.boriselec.morphdict.dom.in.FileLemmaReader;
import com.boriselec.morphdict.dom.out.LemmaWriter;
import com.boriselec.morphdict.load.DictLoader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.bind.Unmarshaller;
import java.util.concurrent.locks.ReentrantLock;

@RestController("/admin")
@RequestMapping("/admin")
public class AdminController {
    private final DictLoader dictLoader;
    private final LemmaWriter dbLemmaWriter;
    private final Unmarshaller unmarshaller;
    private final String xmlPath;

    private final ReentrantLock dictLock = new ReentrantLock();
    private final ReentrantLock dbLock = new ReentrantLock();

    public AdminController(DictLoader dictLoader,
                           LemmaWriter dbLemmaWriter,
                           Unmarshaller unmarshaller,
                           @Value("opencorpora.xml.path") String xmlPath) {
        this.dictLoader = dictLoader;
        this.dbLemmaWriter = dbLemmaWriter;
        this.unmarshaller = unmarshaller;
        this.xmlPath = xmlPath;
    }

    @RequestMapping(value = "/sync/dict", method = RequestMethod.POST)
    public void syncDict() {
        withLock(dictLock, dictLoader::ensureLastVersion);
    }

    @RequestMapping(value = "/sync/db", method = RequestMethod.POST)
    public void syncDb() {
        withLock(dbLock, () -> {
            try (
                LemmaReader in = new FileLemmaReader(unmarshaller, xmlPath);
            ) {
                for (Lemma lemma : in) {
                    dbLemmaWriter.write(lemma);
                }
            }
        });
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
