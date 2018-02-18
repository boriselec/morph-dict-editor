package com.boriselec.morphdict;

import com.boriselec.morphdict.edit.*;
import org.apache.commons.lang3.StringUtils;

import javax.xml.stream.*;
import java.io.*;

public class App {
    public static void main(String[] args) throws IOException, XMLStreamException {
        System.setProperty("line.separator", "\n");

        XMLInputFactory xmlFactory = XMLInputFactory.newInstance();
        XMLOutputFactory outFactory = XMLOutputFactory.newInstance();

        LemmaHandler handler = new ChainLemmaHandler(
            new BlackListTextLemmaFilter("ёж"),
            new DigitLemmaFilter()
        );

        EventFilter xmlTagFilter = new XmlTagFilter("grammemes", "link_types", "links", "restrictions");

        XmlTransformer xmlTransformer = new XmlTransformer(xmlFactory, outFactory, handler, xmlTagFilter);

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
            if (!StringUtils.isBlank(line)) {
                writer.println(line
                    .replaceAll("></g>", "/>")
                    .replaceAll("></link>", "/>")
                    .replaceAll("utf-8\"\\?>", "utf-8\" standalone=\"yes\"?>\n")
                );
            }
            line = br.readLine();
        }

        br.close();
        writer.close();
    }
}
