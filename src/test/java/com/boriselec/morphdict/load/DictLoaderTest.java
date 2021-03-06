package com.boriselec.morphdict.load;

import com.boriselec.morphdict.storage.VersionStorage;
import org.junit.Assert;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DictLoaderTest {
    @Test
    public void testFormat() throws Exception {
        DateTimeFormatter formatter = VersionStorage.VERSION_FORMAT;

        ZonedDateTime time = ZonedDateTime.parse("27.02.2018 05:21 MSK", formatter);

        Assert.assertNotNull(time);
    }
}