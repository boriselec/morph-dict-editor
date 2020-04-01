package com.boriselec.morphdict.dom.edit;

import com.boriselec.morphdict.dom.data.Lemma;
import com.boriselec.morphdict.dom.data.WordForm;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertTrue;

public class DigitLemmaFilterTest {
    @Test
    public void shouldContinueOnLetterWord() throws Exception {
        DigitLemmaFilter filter = new DigitLemmaFilter();
        Lemma lemma = new Lemma();
        lemma.lemmaForm = new WordForm();
        lemma.lemmaForm.text = "test";

        Optional<Lemma> transformed = filter.transform(lemma);

        assertTrue(transformed.isPresent());
    }
}