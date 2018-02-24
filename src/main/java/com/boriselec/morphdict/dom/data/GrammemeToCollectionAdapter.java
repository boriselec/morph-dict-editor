package com.boriselec.morphdict.dom.data;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * extracts
 *
 * <pre>
 * {@code
 *
 *  <g v="NOUN"/>
 * }
 * </pre>
 *
 * into string
 */
public class GrammemeToCollectionAdapter extends XmlAdapter<Object, String> {
    private static final String TAG_NAME = "g";
    private static final String ATTRIBUTE_NAME = "v";

    private DocumentBuilder documentBuilder;

    public GrammemeToCollectionAdapter() throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilder = documentBuilderFactory.newDocumentBuilder();
    }

    @Override
    public String unmarshal(Object value) throws Exception {
        return ((Element) value).getAttribute(ATTRIBUTE_NAME);
    }

    @Override
    public Object marshal(String value) throws Exception {
        Document document = documentBuilder.newDocument();
        Element root = document.createElement(TAG_NAME);
        root.setAttribute(ATTRIBUTE_NAME, value);
        return root;
    }
}
