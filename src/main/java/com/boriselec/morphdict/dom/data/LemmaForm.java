package com.boriselec.morphdict.dom.data;

import com.google.gson.annotations.SerializedName;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;

public class LemmaForm {
    @XmlAttribute(name = "t")
    @SerializedName("t")
    public String text;
    @XmlElement(name = "g")
    @SerializedName("g")
    @XmlJavaTypeAdapter(GrammemeToCollectionAdapter.class)
    public List<String> grammemes;
}
