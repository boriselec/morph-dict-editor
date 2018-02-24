package com.boriselec.morphdict.stax.edit;

import com.boriselec.morphdict.stax.data.LemmaEvent;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BlackListTextLemmaFilterTest {
    @Test
    public void shouldSkipEntryInBlackList() throws Exception {
        BlackListTextLemmaFilter filter = new BlackListTextLemmaFilter("test");
        LemmaEvent lemma = mock(LemmaEvent.class);
        when(lemma.getText()).thenReturn("test");

        LemmaHandler.Action action = filter.handle(lemma);

        assertEquals(LemmaHandler.Action.SKIP, action);
    }

    @Test
    public void shouldContinueOnOtherWord() throws Exception {
        BlackListTextLemmaFilter filter = new BlackListTextLemmaFilter("test");
        LemmaEvent lemma = mock(LemmaEvent.class);
        when(lemma.getText()).thenReturn("notTest");

        LemmaHandler.Action action = filter.handle(lemma);

        assertEquals(LemmaHandler.Action.CONTINUE, action);
    }
}