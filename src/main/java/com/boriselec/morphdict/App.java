package com.boriselec.morphdict;

import com.boriselec.morphdict.edit.XmlTransformer;

import javax.xml.stream.*;
import java.io.*;

public class App {
    public static void main(String[] args) throws IOException, XMLStreamException {
        XMLInputFactory xmlFactory = XMLInputFactory.newInstance();
        XMLOutputFactory outFactory = XMLOutputFactory.newInstance();
        XMLEventFactory xmlEventFactory = XMLEventFactory.newFactory();

        XmlTransformer xmlTransformer = new XmlTransformer(xmlFactory, outFactory, xmlEventFactory);

        InputStream inputStream = new FileInputStream("C:\\Users\\boris\\Downloads\\dict.opcorpora.xml\\dict.opcorpora.xml");
        OutputStream outputStream = new FileOutputStream("C:\\Users\\boris\\Downloads\\dict.opcorpora.xml\\dict.opcorpora.filtered.xml");

        xmlTransformer.transform(inputStream, outputStream);

        inputStream.close();
        outputStream.close();
    }
}
