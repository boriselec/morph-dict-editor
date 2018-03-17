package com.boriselec.morphdict.storage.file;

import com.boriselec.morphdict.storage.VersionStorage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;

/**
 * Stores version in specified file property
 */
public class FileVersionStorage implements VersionStorage {
    private final Path propertyFilename;

    public FileVersionStorage(String propertyFilename) {
        this.propertyFilename = Paths.get(propertyFilename);
    }

    public ZonedDateTime get() {
        if (!Files.exists(propertyFilename)) {
            return null;
        }
        try {
            String currentVersion = String.join("", Files.readAllLines(propertyFilename));
            return ZonedDateTime.parse(currentVersion, VERSION_FORMAT);
        } catch (IOException e) {
            throw new RuntimeException("Cannot read property file: " + e.getMessage(), e);
        }
    }

    public void update(ZonedDateTime currentVersion) {
        try {
            if (Files.exists(propertyFilename)) {
                Files.delete(propertyFilename);
            }
            String version = VERSION_FORMAT.format(currentVersion);
            Files.write(propertyFilename, version.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Cannot read property file: " + e.getMessage(), e);
        }
    }
}
