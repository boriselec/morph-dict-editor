package com.boriselec.morphdict.dom.in;

import com.boriselec.morphdict.dom.data.Lemma;
import com.boriselec.morphdict.dom.edit.LemmaReader;
import com.boriselec.morphdict.dom.edit.LemmaTransformer;
import com.boriselec.morphdict.dom.out.CompositeLemmaWriter;
import com.boriselec.morphdict.dom.out.ConsoleProgressWriter;
import com.boriselec.morphdict.dom.out.LemmaWriter;
import com.boriselec.morphdict.load.DictLoader;
import com.boriselec.morphdict.storage.VersionStorage;
import com.boriselec.morphdict.storage.sql.VersionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.time.ZonedDateTime;
import java.util.concurrent.locks.ReentrantLock;

/**
 * opencorpora.xml -> database
 */
@Component
public class DatabaseDictLoader {
    private static final Logger log = LoggerFactory.getLogger(DatabaseDictLoader.class);

    private final LemmaWriter dbLemmaWriter;
    private final JAXBContext lemmaJaxbContext;
    private final String inXmlPath;
    private final LemmaTransformer lemmaFilter;
    private final ReentrantLock inFileLock;
    private final ReentrantLock dbLock;
    private final VersionStorage versionDao;

    public DatabaseDictLoader(@Qualifier("database") LemmaWriter dbLemmaWriter,
                              JAXBContext lemmaJaxbContext,
                              @Value("${MORPH_FILE_ROOT}") String fileRoot,
                              LemmaTransformer lemmaFilter,
                              @Qualifier("inFileLock") ReentrantLock inFileLock,
                              @Qualifier("dbLock") ReentrantLock dbLock,
                              VersionStorage versionDao) {
        this.dbLemmaWriter = new CompositeLemmaWriter(new ConsoleProgressWriter(log), dbLemmaWriter);
        this.lemmaJaxbContext = lemmaJaxbContext;
        this.inXmlPath = fileRoot + DictLoader.ORIGINAL_FILENAME;
        this.lemmaFilter = lemmaFilter;
        this.inFileLock = inFileLock;
        this.dbLock = dbLock;
        this.versionDao = versionDao;
    }

    @Scheduled(fixedDelayString = "#{${MORPH_DBLOADER_PERIOD_MINUTES} * 60 * 1000}")
    public void load() {
        ZonedDateTime fileVersion = versionDao.get(VersionType.FILE);
        ZonedDateTime storageVersion = versionDao.get(VersionType.STORAGE);

        if (fileVersion == null) {
            log.warn("File is not loaded");
            return;
        }

        if (!fileVersion.equals(storageVersion)) {
            if (inFileLock.tryLock()) {
                try (
                    LemmaReader in = new FileLemmaReader(lemmaJaxbContext.createUnmarshaller(), inXmlPath)
                ) {
                    log.info("Loading {} version in database...", fileVersion);
                    dbLock.lock();
                    for (Lemma lemma : in) {
                        lemmaFilter.transform(lemma)
                            .ifPresent(dbLemmaWriter::write);
                    }
                    versionDao.update(VersionType.STORAGE, fileVersion);
                } catch (JAXBException e) {
                    throw new RuntimeException(e);
                } finally {
                    inFileLock.unlock();
                    dbLock.unlock();
                }
            } else {
                log.warn("In file is locked");
            }
        } else {
            log.trace("Database is up to date {}", storageVersion);
        }
    }
}
