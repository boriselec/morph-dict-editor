package com.boriselec.morphdict.dom.edit;

import com.boriselec.morphdict.dom.data.Lemma;
import com.boriselec.morphdict.dom.data.WordForm;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BlackListTextLemmaFilterTest {
    @Test
    public void shouldSkipEntryInBlackList() throws Exception {
        BlackListTextLemmaFilter filter = new BlackListTextLemmaFilter("test");
        Lemma lemma = new Lemma();
        lemma.lemmaForm = new WordForm();
        lemma.lemmaForm.text = "test";

        Optional<Lemma> transformed = filter.transform(lemma);

        assertFalse(transformed.isPresent());
    }

    @Test
    public void shouldContinueOnOtherWord() throws Exception {
        BlackListTextLemmaFilter filter = new BlackListTextLemmaFilter("test");
        Lemma lemma = new Lemma();
        lemma.lemmaForm = new WordForm();
        lemma.lemmaForm.text = "notTest";

        Optional<Lemma> transformed = filter.transform(lemma);

        assertTrue(transformed.isPresent());
    }
}