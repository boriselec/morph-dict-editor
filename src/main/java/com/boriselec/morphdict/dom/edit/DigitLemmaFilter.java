package com.boriselec.morphdict.dom.edit;

import com.boriselec.morphdict.dom.data.Lemma;

/**
 * Should not contains digits
 */
public class DigitLemmaFilter extends LemmaFilter {
    @Override
    protected boolean isDisabled(Lemma lemma) {
        return lemma.lemmaForm.text.chars().anyMatch(Character::isDigit);
    }
}
