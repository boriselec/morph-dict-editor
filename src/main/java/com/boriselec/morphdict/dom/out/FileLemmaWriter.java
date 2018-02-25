package com.boriselec.morphdict.dom.out;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Abstract writer
 */
public abstract class FileLemmaWriter implements LemmaWriter {
    private final PrintWriter writer;

    protected FileLemmaWriter(String path) throws FileNotFoundException {
        writer = new PrintWriter(path);
    }

    @Override
    public void close() {
        writeEnd();
        writer.close();
    }

    protected abstract void writeEnd();

    protected PrintWriter getWriter() {
        return writer;
    }
}
