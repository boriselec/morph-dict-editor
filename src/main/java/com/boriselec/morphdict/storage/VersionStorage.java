package com.boriselec.morphdict.storage;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Stores last synced dictionary version
 */
public interface VersionStorage {
    DateTimeFormatter VERSION_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm zzz");

    ZonedDateTime get();
    void update(ZonedDateTime version);
}
