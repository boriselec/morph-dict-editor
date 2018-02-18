package com.boriselec.morphdict.edit;

import com.boriselec.morphdict.data.Lemma;

/**
 * Chain of handlers
 */
public class ChainLemmaHandler implements LemmaHandler {
    private final LemmaHandler[] handlers;

    public ChainLemmaHandler(LemmaHandler... handlers) {
        this.handlers = handlers;
    }

    @Override
    public Action handle(Lemma lemma) {
        for (LemmaHandler handler : handlers) {
            switch (handler.handle(lemma)) {
                case CONTINUE:
                    continue;
                case SKIP:
                    return Action.SKIP;
            }
        }
        return Action.CONTINUE;
    }
}
