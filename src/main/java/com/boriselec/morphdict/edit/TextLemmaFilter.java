package com.boriselec.morphdict.edit;

import com.boriselec.morphdict.data.Lemma;

import java.util.function.Function;

/**
 * Filter lemmas by processing its canonical form
 */
public abstract class TextLemmaFilter implements LemmaHandler {
    @Override
    public final Action handle(Lemma lemma) {
        return disabled().apply(lemma.getText()) ? Action.SKIP : Action.CONTINUE;
    }

    protected abstract Function<String, Boolean> disabled();
}
