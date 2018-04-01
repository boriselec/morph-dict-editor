package com.boriselec.morphdict.dom.edit;

import com.boriselec.morphdict.dom.data.Lemma;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

/**
 * Filter lemmas by predefined black list
 */
public class BlackListTextLemmaFilter extends LemmaFilter {
    private final Set<String> blackList;

    public BlackListTextLemmaFilter(String... blackList) {
        this.blackList = new TreeSet<>(Arrays.asList(blackList));
    }

    @Override
    protected boolean isDisabled(Lemma lemma) {
        return blackList.contains(lemma.lemmaForm.text);
    }
}
