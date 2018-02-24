package com.boriselec.morphdict.edit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.util.Arrays;

public class XmlAttributeFilterTest {
    private static final String DISABLED = "disabled";
    private static final String ENABLED = "enabled";
    private static XMLEventFactory EVENT_FACTORY;

    private StartElement twoAttrsEvent;

    @BeforeClass
    public static void setUpClass() throws Exception {
        EVENT_FACTORY = XMLEventFactory.newFactory();
    }

    @Before
    public void setUp() throws Exception {
        twoAttrsEvent = EVENT_FACTORY.createStartElement(
            QName.valueOf("tag"),
            Arrays.asList(
                EVENT_FACTORY.createAttribute(QName.valueOf(DISABLED), "value"),
                EVENT_FACTORY.createAttribute(QName.valueOf(ENABLED), "value")
            ).iterator(),
            null);
    }

    @Test
    public void shouldRemoveDisabledAttr() throws Exception {
        XmlAttributeFilter filter = new XmlAttributeFilter(EVENT_FACTORY, DISABLED);

        XMLEvent trimmed = filter.trim(twoAttrsEvent);

        Assert.assertNull(trimmed.asStartElement().getAttributeByName(QName.valueOf(DISABLED)));
    }

    @Test
    public void shouldNotRemoveEnabled() throws Exception {
        XmlAttributeFilter filter = new XmlAttributeFilter(EVENT_FACTORY, DISABLED);

        XMLEvent trimmed = filter.trim(twoAttrsEvent);

        Assert.assertNotNull(trimmed.asStartElement().getAttributeByName(QName.valueOf(ENABLED)));
    }
}