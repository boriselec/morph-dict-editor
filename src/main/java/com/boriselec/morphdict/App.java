package com.boriselec.morphdict;

import com.boriselec.morphdict.edit.*;
import de.odysseus.staxon.json.JsonXMLConfig;
import de.odysseus.staxon.json.JsonXMLConfigBuilder;
import de.odysseus.staxon.json.JsonXMLOutputFactory;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.*;
import java.io.*;

public class App {
    public static void main(String[] args) throws IOException, XMLStreamException, ParserConfigurationException, SAXException {
        System.setProperty("line.separator", "\n");

        XMLInputFactory xmlFactory = XMLInputFactory.newInstance();
        XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newFactory();
        XMLEventFactory eventFactory = XMLEventFactory.newFactory();
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        JsonXMLConfig config = new JsonXMLConfigBuilder()
            .autoArray(true)
            .prettyPrint(true)
            .build();
        XMLOutputFactory jsonOutputFactory = new JsonXMLOutputFactory(config);

        LemmaHandler handler = new ChainLemmaHandler(
            new BlackListTextLemmaFilter("ёж"),
            new DigitLemmaFilter()
        );

        EventFilter xmlTagFilter = new XmlTagFilter("grammemes", "link_types", "links", "restrictions");
        XmlAttributeFilter xmlAttrFilter = new XmlAttributeFilter(eventFactory, "id", "rev");

        XmlTransformer xmlTransformer = new XmlTransformer(xmlFactory, xmlOutputFactory, handler, xmlTagFilter, xmlAttrFilter);

        InputStream inputStream = new FileInputStream("C:\\Users\\boris\\Downloads\\dict.opcorpora.xml\\dict.opcorpora.xml");
        OutputStream outputStream = new FileOutputStream("C:\\Users\\boris\\Downloads\\dict.opcorpora.xml\\dict.opcorpora.filtered.tmp.xml");

        xmlTransformer.transform(inputStream, outputStream);

        inputStream.close();
        outputStream.close();

        postProcessXml();
        convertToJson(documentBuilder);
    }

    private static void convertToJson(DocumentBuilder documentBuilder) throws IOException, ParserConfigurationException, SAXException {
        PrintWriter writer = new PrintWriter("C:\\Users\\boris\\Downloads\\dict.opcorpora.xml\\dict.opcorpora.filtered.json", "UTF-8");

        BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\boris\\Downloads\\dict.opcorpora.xml\\dict.opcorpora.filtered.xml"));
        String line = br.readLine();

        boolean firstLemma = true;
        while (line != null) {
            if (line.contains("?xml")) {
                writer.println("{");
            } else if (line.contains("dictionary version")) {
                writer.println("\"dictionary\": {");
            } else if (line.contains("<lemmata")) {
                writer.println("\"lemmata\": [");
            } else if (line.contains("<lemma>")) {
                printLemma(writer, line, firstLemma, documentBuilder);
                firstLemma = false;
            } else if (line.contains("</lemmata>")) {
                writer.println("]");
            } else if (line.contains("</dictionary>")) {
                writer.println("}");
                writer.println("}");
            } else {
                throw new IllegalStateException("Unknown: " + line);
            }
            line = br.readLine();
        }

        br.close();
        writer.close();
    }

    private static void postProcessXml() throws IOException {
        PrintWriter writer = new PrintWriter("C:\\Users\\boris\\Downloads\\dict.opcorpora.xml\\dict.opcorpora.filtered.xml", "UTF-8");

        BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\boris\\Downloads\\dict.opcorpora.xml\\dict.opcorpora.filtered.tmp.xml"));
        String line = br.readLine();

        while (line != null) {
            if (!StringUtils.isBlank(line)) {
                writer.println(line
                    .replaceAll("></g>", "/>")
                    .replaceAll("f( [^<]+)></f>", "f$1/>")
                    .replaceAll("></link>", "/>")
                    .replaceAll("utf-8\"\\?>", "utf-8\" standalone=\"yes\"?>\n")
                );
            }
            line = br.readLine();
        }

        br.close();
        writer.close();
    }

    private static void printLemma(PrintWriter writer, String line, boolean firstLemma, DocumentBuilder documentBuilder) throws ParserConfigurationException, IOException, SAXException {
        Document dom = documentBuilder.parse(new InputSource(new ByteArrayInputStream(line.getBytes("utf-8"))));
        dom.getDocumentElement().normalize();

        StringBuilder json = new StringBuilder(firstLemma ? "{" : ",{");

        Element l = (Element) dom.getElementsByTagName("l").item(0);
        json.append("\"l\":{");
        String lt = l.getAttribute("t");
        json.append("\"t\":\"").append(lt).append("\"");

        NodeList gs = l.getElementsByTagName("g");
        if (gs != null) {
            json.append(",\"g\":[");
            for (int i = 0; i < gs.getLength(); i++) {
                if (i > 0) {
                    json.append(",");
                }
                Element g = (Element) gs.item(i);
                String v = g.getAttribute("v");
                json.append("\"").append(v).append("\"");
            }
            json.append("]");
        }

        json.append("}");

        NodeList fs = dom.getElementsByTagName("f");
        if (fs != null) {
            json.append(",\"f\":[");
            for (int i = 0; i < fs.getLength(); i++) {
                if (i > 0) {
                    json.append(",");
                }
                Element f = (Element) fs.item(i);

                json.append("{");

                String ft = f.getAttribute("t");
                json.append("\"t\":\"").append(ft).append("\"");

                NodeList fgs = f.getElementsByTagName("g");
                if (fgs != null) {
                    json.append(",\"g\":[");
                    for (int j = 0; j < fgs.getLength(); j++) {
                        if (j > 0) {
                            json.append(",");
                        }
                        Element g = (Element) fgs.item(j);
                        String v = g.getAttribute("v");
                        json.append("\"").append(v).append("\"");
                    }
                    json.append("]");
                }
                json.append("}");
            }
            json.append("]");
        }

        json.append("}");

        writer.println(json);
    }
}
