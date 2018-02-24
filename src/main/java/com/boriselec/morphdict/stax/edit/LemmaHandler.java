package com.boriselec.morphdict.stax.edit;

import com.boriselec.morphdict.stax.data.LemmaEvent;

/**
 * Action on lemma
 */
public interface LemmaHandler {
    Action handle(LemmaEvent lemma);

    enum Action {
        SKIP,
        CONTINUE,
    }
}
