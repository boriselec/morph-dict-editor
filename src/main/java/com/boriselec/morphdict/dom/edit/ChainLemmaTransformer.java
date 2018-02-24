package com.boriselec.morphdict.dom.edit;

import com.boriselec.morphdict.dom.data.Lemma;

import java.util.Optional;

public class ChainLemmaTransformer implements LemmaTransformer {
    private final LemmaTransformer[] transformers;

    public ChainLemmaTransformer(LemmaTransformer... transformers) {
        this.transformers = transformers;
    }

    @Override
    public Optional<Lemma> transform(Lemma lemma) {
        Lemma transformed = lemma;
        for (LemmaTransformer transformer : transformers) {
            Optional<Lemma> result = transformer.transform(transformed);
            if (result.isPresent()) {
                transformed = result.get();
            } else {
                return Optional.empty();
            }
        }
        return Optional.of(transformed);
    }
}
