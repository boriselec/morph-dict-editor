package com.boriselec.morphdict.dom.edit;

import com.boriselec.morphdict.dom.data.Lemma;

import java.util.Optional;

public abstract class LemmaFilter implements LemmaTransformer {
    @Override
    public final Optional<Lemma> transform(Lemma lemma) {
        return disabled(lemma) ? Optional.empty() : Optional.of(lemma);
    }

    protected abstract boolean disabled(Lemma lemma);
}
