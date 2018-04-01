package com.boriselec.morphdict.dom.edit;

import com.boriselec.morphdict.dom.data.Lemma;
import com.boriselec.morphdict.dom.data.LemmaState;

import java.util.Optional;

public abstract class LemmaFilter implements LemmaTransformer {
    @Override
    public final Optional<Lemma> transform(Lemma lemma) {
        lemma.state = isDisabled(lemma) ? LemmaState.DELETED : LemmaState.OPENCORPORA;
        return Optional.of(lemma);
    }

    protected abstract boolean isDisabled(Lemma lemma);
}
