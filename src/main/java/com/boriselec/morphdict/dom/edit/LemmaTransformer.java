package com.boriselec.morphdict.dom.edit;

import com.boriselec.morphdict.dom.data.Lemma;

import java.util.Optional;

/**
 * Transforms lemmas
 */
public interface LemmaTransformer {
    /**
     * @param lemma dictionary entry
     * @return transformed (empty if skip)
     */
    Optional<Lemma> transform(Lemma lemma);
}
