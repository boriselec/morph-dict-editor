package com.boriselec.morphdict.stax.edit;

import java.util.function.Function;

/**
 * Should not contains digits
 */
public class DigitLemmaFilter extends TextLemmaFilter {
    @Override
    protected Function<String, Boolean> disabled() {
        return str -> str.chars().anyMatch(Character::isDigit);
    }
}
