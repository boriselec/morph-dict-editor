package com.boriselec.morphdict.storage;

import com.boriselec.morphdict.storage.sql.VersionType;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Stores last synced dictionary version
 */
public interface VersionStorage {
    DateTimeFormatter VERSION_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm zzz");

    ZonedDateTime get(VersionType type);
    void update(VersionType type, ZonedDateTime version);
}
