package com.boriselec.morphdict.dom.out;

import com.boriselec.morphdict.dom.data.Lemma;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Abstract writer
 */
public abstract class LemmaWriter {
    private final PrintWriter writer;

    protected LemmaWriter(String path) throws FileNotFoundException {
        writer = new PrintWriter(path);
    }

    public abstract void write(Lemma lemma);

    public void close() {
        writeEnd();
        writer.close();
    }

    protected abstract void writeEnd();

    protected PrintWriter getWriter() {
        return writer;
    }
}
