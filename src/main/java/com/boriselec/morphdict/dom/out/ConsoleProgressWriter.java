package com.boriselec.morphdict.dom.out;

import com.boriselec.morphdict.dom.data.Lemma;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("console")
public class ConsoleProgressWriter implements LemmaWriter {
    private static final Logger log = LoggerFactory.getLogger(ConsoleProgressWriter.class);

    private int count = 0;
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
