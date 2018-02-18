package com.boriselec.morphdict.edit;

import com.boriselec.morphdict.data.Lemma;

/**
 * Action on lemma
 */
public interface LemmaHandler {
    Action handle(Lemma lemma);

    enum Action {
        SKIP,
        CONTINUE,
    }
}
