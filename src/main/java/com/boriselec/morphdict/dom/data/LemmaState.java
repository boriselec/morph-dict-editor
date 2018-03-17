package com.boriselec.morphdict.dom.data;

public enum LemmaState {
    /**
     * original opencorpora entry
     */
    OPENCORPORA(0),

    /**
     * inserted or edited entry
     */
    MANUAL(1),

    /**
     * filtered out or manually removed entry
     */
    DELETED(2);

    private final int code;

    LemmaState(int i) {
        code = i;
    }

    public int getCode() {
        return code;
    }
}
