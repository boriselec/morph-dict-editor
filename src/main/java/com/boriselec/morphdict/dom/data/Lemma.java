package com.boriselec.morphdict.dom.data;

import com.google.gson.annotations.SerializedName;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;

/**
 * LemmaEvent
 *
 * <pre>
 * {@code
 *
 *     <lemma id="1" rev="402007">
 *         <l t="абажур"><g v="NOUN"/><g v="inan"/><g v="masc"/></l>
 *         <f t="абажур"><g v="sing"/><g v="nomn"/></f>
 *         <f t="абажура"><g v="sing"/><g v="gent"/></f>
 *         ...
 *     </lemma>
 * }
 * </pre>
 */
@XmlRootElement(name = "lemma")
public class Lemma {
    @XmlElement(name = "l")
    @SerializedName("l")
    public LemmaForm lemmaForm;
    @XmlElement(name = "f")
    @SerializedName("f")
    public List<WordForm> wordForms;

    public Lemma() {
    }

    private Lemma(LemmaForm lemmaForm, List<WordForm> wordForms) {
        this.lemmaForm = Objects.requireNonNull(lemmaForm);
        this.wordForms = Objects.requireNonNull(wordForms);
    }

    public static final class Builder {
        private LemmaForm lemmaForm;
        private List<WordForm> wordForms = new LinkedList<>();

        public Builder addLemma(String text, String... grammemes) {
            lemmaForm = new LemmaForm();
            lemmaForm.text = text;
            lemmaForm.grammemes = grammemes != null ? Arrays.asList(grammemes) : Collections.emptyList();
            return this;
        }

        public Builder addForm(String text, String... grammemes) {
            WordForm wordForm = new WordForm();
            wordForm.text = text;
            wordForm.grammemes = grammemes != null ? Arrays.asList(grammemes) : Collections.emptyList();
            wordForms.add(wordForm);
            return this;
        }

        public Lemma build() {
            return new Lemma(lemmaForm, wordForms);
        }
    }
}
