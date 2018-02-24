package com.boriselec.morphdict.dom.edit;

import com.boriselec.morphdict.dom.data.Lemma;

import java.util.Optional;

/**
 * Transforms lemmas
 */
public class LemmaTransformer {
    /**
     * @param lemma dictionary entry
     * @return transformed (empty if skip)
     */
    public Optional<Lemma> transform(Lemma lemma) {
        return Optional.of(lemma);
    }
}
