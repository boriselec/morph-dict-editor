package com.boriselec.morphdict.load;

import com.boriselec.morphdict.storage.VersionStorage;
import com.boriselec.morphdict.storage.sql.VersionType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;

import static com.boriselec.morphdict.storage.VersionStorage.VERSION_FORMAT;

@Component
public class DictLoader {
    private static final Logger log = LoggerFactory.getLogger(DictLoader.class);

    private static final String OPEN_CORPORA_PAGE = "http://opencorpora.org/?page=downloads";
    private static final String DICT_DOWNLOAD = "http://opencorpora.org/files/export/dict/dict.opcorpora.xml.zip";

    private final Path destinationPath;
    private final String tempZipPath;
    private final VersionStorage versionStorage;

    private final ReentrantLock inFileLock;

    public DictLoader(@Value("${opencorpora.xml.path}") String destinationPath,
                      @Value("${temp.zip.path}") String tempZipPath,
                      VersionStorage versionStorage,
                      @Qualifier("inFileLock") ReentrantLock inFileLock) {
        this.destinationPath = Paths.get(destinationPath);
        this.tempZipPath = tempZipPath;
        this.versionStorage = versionStorage;
        this.inFileLock = inFileLock;
    }

    public void ensureLastVersion() {
        if (inFileLock.tryLock()) {
            try {
                ZonedDateTime localVersion = versionStorage.get(VersionType.FILE);
                log.info("local dictionary version is {}", localVersion);
                ZonedDateTime currentVersion = getCurrentVersion();
                log.info("current dictionary version is {}", currentVersion);

                if (!Files.exists(destinationPath) || !currentVersion.equals(localVersion)) {
                    deleteOld();
                    load();
                    unzip();
                    versionStorage.update(VersionType.FILE, currentVersion);
                }
            } catch (IOException e) {
                throw new IllegalStateException(e.getMessage(), e);
            } finally {
                inFileLock.unlock();
            }
        } else {
            log.warn("Already in progress");
        }
    }

    private ZonedDateTime getCurrentVersion() throws IOException {
        Document loadPage = Jsoup.connect(OPEN_CORPORA_PAGE).get();
        List<Element> infoTags = loadPage.getElementsByTag("p").stream()
            .filter(e -> e.text().startsWith("XML ("))
            .collect(Collectors.toList());

        if (infoTags.size() != 1) {
            throw new IllegalStateException("Cannot find dictionary version info");
        }

        Element info = infoTags.iterator().next();

        return extractVersion(info.text());
    }

    private ZonedDateTime extractVersion(String text) {
        //XML ( XML Schema), обновлён 27.02.2018 05:21 MSK, см. описание формата
        String updatedOn = text.substring(28, 48);
        return ZonedDateTime.parse(updatedOn, VERSION_FORMAT);
    }

    private void deleteOld() throws IOException {
        if (Files.exists(destinationPath)) {
            Files.delete(destinationPath);
        }
    }

    private void load() throws IOException {
        log.info("Loading dictionary...");
        URL downloadUrl = new URL(DICT_DOWNLOAD);
        try (
            ReadableByteChannel rbc = Channels.newChannel(downloadUrl.openStream());
            FileOutputStream fos = new FileOutputStream(tempZipPath);
        ) {
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }
        log.info("Dictionary loaded");
    }

    private void unzip() throws IOException {
        log.info("Unzipping...");
        try (
            ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(new FileInputStream(tempZipPath)));
            FileOutputStream unpack = new FileOutputStream(destinationPath.toFile());
        ) {
            zipInputStream.getNextEntry();
            unpack.getChannel().transferFrom(Channels.newChannel(zipInputStream), 0, Long.MAX_VALUE);
        } finally {
            Files.delete(Paths.get(tempZipPath));
        }
        log.info("Unzipped");
    }
}
