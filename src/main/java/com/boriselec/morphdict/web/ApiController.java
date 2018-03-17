package com.boriselec.morphdict.web;

import com.boriselec.morphdict.dom.data.Lemma;
import com.boriselec.morphdict.storage.sql.LemmaDao;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/api")
@RequestMapping("/api")
public class ApiController {
    private final LemmaDao dao;
    private final Gson gson;

    public ApiController(LemmaDao dao, Gson gson) {
        this.dao = dao;
        this.gson = gson;
    }

    /**
     * [{
     *  "l":{"t":"ёж","g":[...]},
     *  "f":[...],
     *  "id":1
     * }]
     */
    @RequestMapping(value = "/lemma", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getLemma(
        @RequestParam(value = "from") int from,
        @RequestParam(value = "to") int to)
    {
        List<Lemma> lemmata = dao.get(from, to, s -> gson.fromJson(s, Lemma.class));
        return gson.toJson(lemmata, new TypeToken<List<Lemma>>(){}.getType());
    }

    @RequestMapping(value = "/lemma", method = RequestMethod.DELETE)
    public void deleteLemma(
        @RequestParam(value = "id") int id)
    {
        dao.delete(id);
    }
}
