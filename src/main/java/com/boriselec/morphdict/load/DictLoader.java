package com.boriselec.morphdict.load;

import com.boriselec.morphdict.storage.VersionStorage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

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
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;

import static com.boriselec.morphdict.storage.VersionStorage.VERSION_FORMAT;

public class DictLoader {
    private static final String OPEN_CORPORA_PAGE = "http://opencorpora.org/?page=downloads";
    private static final String DICT_DOWNLOAD = "http://opencorpora.org/files/export/dict/dict.opcorpora.xml.zip";

    private static final String TEMP_ZIP = "C:\\Users\\boris\\Downloads\\dict.opcorpora.xml\\dict.opcorpora.xml.zip";

    private final Path destinationPath;
    private final VersionStorage versionStorage;

    public DictLoader(String destinationPath, VersionStorage versionStorage) {
        this.destinationPath = Paths.get(destinationPath);
        this.versionStorage = versionStorage;
    }

    public void ensureLastVersion() throws IOException {
        ZonedDateTime localVersion = versionStorage.get();
        ZonedDateTime currentVersion = getCurrentVersion();

        if (!Files.exists(destinationPath) || !currentVersion.equals(localVersion)) {
            deleteOld();
            load();
            unzip();
            versionStorage.update(currentVersion);
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
        URL downloadUrl = new URL(DICT_DOWNLOAD);
        try (
            ReadableByteChannel rbc = Channels.newChannel(downloadUrl.openStream());
            FileOutputStream fos = new FileOutputStream(TEMP_ZIP);
        ) {
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }
    }

    private void unzip() throws IOException {
        try (
            ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(new FileInputStream(TEMP_ZIP)));
            FileOutputStream unpack = new FileOutputStream(destinationPath.toFile());
        ) {
            zipInputStream.getNextEntry();
            unpack.getChannel().transferFrom(Channels.newChannel(zipInputStream), 0, Long.MAX_VALUE);
        } finally {
            Files.delete(Paths.get(TEMP_ZIP));
        }
    }
}
