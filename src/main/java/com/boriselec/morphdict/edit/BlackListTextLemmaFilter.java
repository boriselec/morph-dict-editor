package com.boriselec.morphdict.edit;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;

/**
 * Filter lemmas by predefined black list
 */
public class BlackListTextLemmaFilter extends TextLemmaFilter {
    private final Set<String> blackList;

    public BlackListTextLemmaFilter(String... blackList) {
        this.blackList = new TreeSet<>(Arrays.asList(blackList));
    }

    @Override
    protected Function<String, Boolean> disabled() {
        return blackList::contains;
    }
}
