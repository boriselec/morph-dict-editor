package com.boriselec.morphdict.stax.edit;

import com.boriselec.morphdict.stax.data.LemmaEvent;

import java.util.function.Function;

/**
 * Filter lemmas by processing its canonical form
 */
public abstract class TextLemmaFilter implements LemmaHandler {
    @Override
    public final Action handle(LemmaEvent lemma) {
        return disabled().apply(lemma.getText()) ? Action.SKIP : Action.CONTINUE;
    }

    protected abstract Function<String, Boolean> disabled();
}
