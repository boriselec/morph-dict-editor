package com.boriselec.morphdict.dom.data;

import com.google.gson.annotations.SerializedName;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

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
}
