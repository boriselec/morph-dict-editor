package com.boriselec.morphdict.link;

import com.boriselec.morphdict.dom.data.Lemma;
import com.boriselec.morphdict.dom.edit.LemmaReader;
import com.boriselec.morphdict.dom.in.DatabaseLemmaReader;
import com.boriselec.morphdict.dom.out.CompositeLemmaWriter;
import com.boriselec.morphdict.dom.out.ConsoleProgressWriter;
import com.boriselec.morphdict.dom.out.LemmaWriter;
import com.boriselec.morphdict.storage.sql.LemmaDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Repo of all dictionary files
 */
@Component
public class FileDictRepository {
    private static final Logger log = LoggerFactory.getLogger(FileDictRepository.class);

    private final List<DictionaryLink> links;
    private final LemmaDao lemmaDao;
    private final DictionaryLinkDao dictionaryLinkDao;
    private final ReentrantLock lock;

    public FileDictRepository(List<DictionaryLink> links,
                              LemmaDao lemmaDao,
                              DictionaryLinkDao dictionaryLinkDao,
                              @Qualifier("dbLock") ReentrantLock lock) {
        this.lemmaDao = lemmaDao;
        this.dictionaryLinkDao = dictionaryLinkDao;
        this.links = init(links);
        this.lock = lock;
    }

    private List<DictionaryLink> init(List<DictionaryLink> links) {
        for (DictionaryLink link : links) {
            dictionaryLinkDao.getRevision(link.getDescription())
                .ifPresent(link::setRevision);
        }
        dictionaryLinkDao.load(links.stream()
            .map(DictionaryLink::getDescription)
            .collect(Collectors.toList()));
        return links;
    }

    /**
     * Update to current revision
     */
    public void update() {
        int currentRevision = lemmaDao.getDictionaryRevision();
        for (DictionaryLink link : links) {
            if (link.getRevision() == null || link.getRevision() != currentRevision) {
                if (lock.tryLock()) {
                    try {
                        log.info("Dictionary {} is outdated: {}. Updating to {}",
                            link.getDescription(), link.getRevision(), currentRevision);
                        writeDict(link::getWriter);
                        link.setRevision(currentRevision);
                        dictionaryLinkDao.updateRevision(link.getDescription(), currentRevision);
                    } finally {
                        lock.unlock();
                    }
                } else {
                    log.warn("Cannot acquire lock");
                }
            } else {
                log.trace("Dictionary {} is up to date: {}", link.getDescription(), currentRevision);
            }
        }

    }

    public List<DictionaryLink> getLinks() {
        return links;
    }

    public Optional<DictionaryLink> getLinkByDescription(String description) {
        return links.stream()
            .filter(link -> link.getDescription().equals(description))
            .findAny();
    }

    private void writeDict(Supplier<LemmaWriter> lemmaWriter) {
        try (
            LemmaReader in = new DatabaseLemmaReader(lemmaDao);
            LemmaWriter out = new CompositeLemmaWriter(
                new ConsoleProgressWriter(log),
                lemmaWriter.get());
        ) {
            for (Lemma lemma : in) {
                out.write(lemma);
            }
        }
    }
}
