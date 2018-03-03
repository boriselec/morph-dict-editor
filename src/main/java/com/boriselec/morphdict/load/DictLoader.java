package com.boriselec.morphdict.load;

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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;

public class DictLoader {
    private static final String OPEN_CORPORA_PAGE = "http://opencorpora.org/?page=downloads";
    private static final String DICT_DOWNLOAD = "http://opencorpora.org/files/export/dict/dict.opcorpora.xml.zip";

    private static final Path VERSION_PROPERTY = Paths.get("C:\\Users\\boris\\Downloads\\dict.opcorpora.xml\\.dictversion");
    private static final String TEMP_ZIP = "C:\\Users\\boris\\Downloads\\dict.opcorpora.xml\\dict.opcorpora.xml.zip";

    static final DateTimeFormatter VERSION_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm zzz");

    private final Path destinationPath;

    public DictLoader(String destinationPath) {
        this.destinationPath = Paths.get(destinationPath);
    }

    public void ensureLastVersion() throws IOException {
        ZonedDateTime localVersion = getLocalVersion();
        ZonedDateTime currentVersion = getCurrentVersion();

        if (!Files.exists(destinationPath) || !currentVersion.equals(localVersion)) {
            deleteOld();
            load();
            unzip();
            updateLocalVersion(currentVersion);
        }
    }

    private ZonedDateTime getLocalVersion() throws IOException {
        if (!Files.exists(VERSION_PROPERTY)) {
            return null;
        }
        String currentVersion = String.join("", Files.readAllLines(VERSION_PROPERTY));
        return ZonedDateTime.parse(currentVersion, VERSION_FORMAT);
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

    private void updateLocalVersion(ZonedDateTime currentVersion) throws IOException {
        if (Files.exists(VERSION_PROPERTY)) {
            Files.delete(VERSION_PROPERTY);
        }
        String version = VERSION_FORMAT.format(currentVersion);
        Files.write(VERSION_PROPERTY, version.getBytes());
    }
}
