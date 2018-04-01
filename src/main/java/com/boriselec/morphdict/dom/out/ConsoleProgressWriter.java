package com.boriselec.morphdict.dom.out;

import com.boriselec.morphdict.dom.data.Lemma;
import org.springframework.stereotype.Component;

@Component("console")
public class ConsoleProgressWriter implements LemmaWriter {
    private int count = 0;
    @Override
    public void write(Lemma lemma) {
        count++;
        if (count % 1000 == 0) {
            System.out.println(count);
        }
    }

    @Override
    public void close() {
        count = 0;
    }
}
