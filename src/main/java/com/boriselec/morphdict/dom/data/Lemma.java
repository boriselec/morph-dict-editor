package com.boriselec.morphdict.dom.data;

import com.google.gson.annotations.SerializedName;

import javax.xml.bind.annotation.XmlAttribute;
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
    public WordForm lemmaForm;
    @XmlElement(name = "f")
    @SerializedName("f")
    public List<WordForm> wordForms;
    @XmlAttribute(name = "id")
    public Integer id;
    @XmlAttribute(name = "rev")
    public Integer revision;

    public LemmaState state;

    public Lemma() {
    }

    private Lemma(WordForm lemmaForm, List<WordForm> wordForms) {
        this.lemmaForm = Objects.requireNonNull(lemmaForm);
        this.wordForms = Objects.requireNonNull(wordForms);
        this.state = LemmaState.MANUAL;
    }

    public static final class Builder {
        private WordForm lemmaForm;
        private List<WordForm> wordForms = new LinkedList<>();

        public Builder addLemma(String text, String... grammemes) {
            lemmaForm = create(text, grammemes);
            return this;
        }

        public Builder addForm(String text, String... grammemes) {
            wordForms.add(create(text, grammemes));
            return this;
        }

        private WordForm create(String text, String... grammemes) {
            WordForm wordForm = new WordForm();
            wordForm.text = text;
            wordForm.grammemes = grammemes != null ? Arrays.asList(grammemes) : Collections.emptyList();
            return wordForm;
        }

        public Lemma build() {
            return new Lemma(lemmaForm, wordForms);
        }
    }
}
