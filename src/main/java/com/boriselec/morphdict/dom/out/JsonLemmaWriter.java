package com.boriselec.morphdict.dom.out;

import com.boriselec.morphdict.dom.data.Lemma;
import com.google.gson.Gson;

/**
 * JSON writer
 */
public class JsonLemmaWriter extends FileLemmaWriter {
    private final Gson gson;
    private boolean first = true;

    public JsonLemmaWriter(Gson gson, String path) {
        super(path);
        this.gson = gson;
        writeHeader();
    }

    private void writeHeader() {
        getWriter().println("{");
        getWriter().println("\"dictionary\":{");
        getWriter().println("\"lemmata\":[");
    }

    @Override
    public void write(Lemma lemma) {
        writeComma();
        getWriter().print(serialize(lemma));
    }

    private void writeComma() {
        if (first) {
            first = false;
        } else {
            getWriter().print(',');
            getWriter().println();
        }
    }

    @Override
    protected void writeEnd() {
        getWriter().println();
        getWriter().println("]");
        getWriter().println("}");
        getWriter().println("}");
    }

    private String serialize(Lemma lemma) {
        return gson.toJson(lemma);
    }
}
