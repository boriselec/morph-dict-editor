package com.boriselec.morphdict.stax.edit;

import com.boriselec.morphdict.stax.data.LemmaEvent;

/**
 * Chain of handlers
 */
public class ChainLemmaHandler implements LemmaHandler {
    private final LemmaHandler[] handlers;

    public ChainLemmaHandler(LemmaHandler... handlers) {
        this.handlers = handlers;
    }

    @Override
    public Action handle(LemmaEvent lemma) {
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
