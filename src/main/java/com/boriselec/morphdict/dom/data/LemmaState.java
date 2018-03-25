package com.boriselec.morphdict.dom.data;

import com.google.gson.annotations.SerializedName;

public enum LemmaState {
    /**
     * original opencorpora entry
     */
    @SerializedName("0")
    OPENCORPORA(0),

    /**
     * inserted or edited entry
     */
    @SerializedName("1")
    MANUAL(1),

    /**
     * filtered out or manually removed entry
     */
    @SerializedName("2")
    DELETED(2);

    private final int code;

    LemmaState(int i) {
        code = i;
    }

    public int getCode() {
        return code;
    }

    public static LemmaState fromCode(int code) {
        for (LemmaState lemmaState : values()) {
            if (lemmaState.code == code) {
                return lemmaState;
            }
        }
        throw new IllegalStateException("Unknown code: " + code);
    }
}
