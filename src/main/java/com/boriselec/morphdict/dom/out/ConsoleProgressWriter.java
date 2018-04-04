package com.boriselec.morphdict.dom.out;

import com.boriselec.morphdict.dom.data.Lemma;
import org.slf4j.Logger;

public class ConsoleProgressWriter implements LemmaWriter {
    private final Logger log;

    private int count = 0;

    public ConsoleProgressWriter(Logger log) {
        this.log = log;
    }

    @Override
    public void write(Lemma lemma) {
        count++;
        if (count % 1000 == 0) {
            log.info("{} processed", count);
        }
    }

    @Override
    public void close() {
        count = 0;
    }
}
