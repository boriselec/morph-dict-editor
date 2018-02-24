package com.boriselec.morphdict.dom.edit;

import com.boriselec.morphdict.dom.data.Lemma;
import org.xml.sax.InputSource;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Parse and iterate lemmas through input
 */
public class LemmaReader implements Iterator<Lemma>, Iterable<Lemma> {
    private final Unmarshaller unmarshaller;
    private final BufferedReader reader;
    private Lemma current;

    public LemmaReader(Unmarshaller unmarshaller, String path) throws FileNotFoundException {
        this.unmarshaller = unmarshaller;
        reader = new BufferedReader(new FileReader(path));
    }

    @Override
    public boolean hasNext() {
        ensureCurrent();
        return current != null;
    }

    @Override
    public Lemma next() {
        ensureCurrent();
        if (current == null) {
            throw new NoSuchElementException();
        }
        Lemma result = current;
        current = null;
        return result;
    }

    @Override
    public Iterator<Lemma> iterator() {
        return this;
    }

    public void close() throws IOException {
        reader.close();
    }

    private void ensureCurrent() {
        if (current == null) {
            current = read();
        }
    }

    private Lemma read() {
        try {
            String line = reader.readLine();
            while (line != null) {
                if (isLemma(line)) {
                    return deserialize(line);
                }
                line = reader.readLine();
            }
            return null;
        } catch (IOException | JAXBException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private Lemma deserialize(String line) throws UnsupportedEncodingException, JAXBException {
        return (Lemma) unmarshaller.unmarshal(new InputSource(new ByteArrayInputStream(line.getBytes("utf-8"))));
    }

    private boolean isLemma(String line) {
        return line.contains("<lemma ");
    }
}
