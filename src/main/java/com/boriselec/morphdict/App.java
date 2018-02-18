package com.boriselec.morphdict;

import com.boriselec.morphdict.edit.XmlTransformer;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.*;

public class App {
    public static void main(String[] args) throws IOException, XMLStreamException {
        XMLInputFactory xmlFactory = XMLInputFactory.newInstance();
        XMLOutputFactory outFactory = XMLOutputFactory.newInstance();
        XMLEventFactory xmlEventFactory = XMLEventFactory.newFactory();

        XmlTransformer xmlTransformer = new XmlTransformer(xmlFactory, outFactory, xmlEventFactory);

        InputStream inputStream = new FileInputStream("C:\\Users\\boris\\Downloads\\dict.opcorpora.xml\\dict.opcorpora.xml");
        OutputStream outputStream = new FileOutputStream("C:\\Users\\boris\\Downloads\\dict.opcorpora.xml\\dict.opcorpora.filtered.tmp.xml");

        xmlTransformer.transform(inputStream, outputStream);

        inputStream.close();
        outputStream.close();

        postProcess();
    }

    private static void postProcess() throws IOException {
        PrintWriter writer = new PrintWriter("C:\\Users\\boris\\Downloads\\dict.opcorpora.xml\\dict.opcorpora.filtered.xml", "UTF-8");

        BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\boris\\Downloads\\dict.opcorpora.xml\\dict.opcorpora.filtered.tmp.xml"));
        String line = br.readLine();

        while (line != null) {
            writer.println(line
                .replaceAll("></g>", "/>")
                .replaceAll("></link>", "/>")
                .replaceAll("utf-8\"\\?>", "utf-8\" standalone=\"yes\"?>\n")
            );
            line = br.readLine();
        }

        br.close();
        writer.close();
    }
}
