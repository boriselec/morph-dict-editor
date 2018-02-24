package com.boriselec.morphdict.stax.edit;

import com.boriselec.morphdict.stax.data.LemmaEvent;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DigitLemmaFilterTest {
    @Test
    public void shouldSkipOnDigitWord() throws Exception {
        DigitLemmaFilter filter = new DigitLemmaFilter();
        LemmaEvent lemma = mock(LemmaEvent.class);
        when(lemma.getText()).thenReturn("test1");

        LemmaHandler.Action action = filter.handle(lemma);

        assertEquals(LemmaHandler.Action.SKIP, action);
    }

    @Test
    public void shouldContinueOnLetterWord() throws Exception {
        DigitLemmaFilter filter = new DigitLemmaFilter();
        LemmaEvent lemma = mock(LemmaEvent.class);
        when(lemma.getText()).thenReturn("test");

        LemmaHandler.Action action = filter.handle(lemma);

        assertEquals(LemmaHandler.Action.CONTINUE, action);
    }
}