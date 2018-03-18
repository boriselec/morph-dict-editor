package com.boriselec.morphdict.web;

import com.boriselec.morphdict.dom.data.Lemma;
import com.boriselec.morphdict.storage.sql.LemmaDao;
import com.boriselec.morphdict.web.view.LemmaView;
import com.google.gson.Gson;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
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
     * {"meta": {
     *  "total": 200
     * },
     * "comments": [{
     *  "l":{"t":"ёж","g":[...]},
     *  "f":[...],
     *  "id":1
     * }]
     */
    @RequestMapping(value = "/lemma", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public LemmaView getLemma(
        @RequestParam(value = "offset") int offset,
        @RequestParam(value = "limit") int limit)
    {
        List<Lemma> lemmata = dao.get(offset, limit, (id, s) -> {
            Lemma lemma = gson.fromJson(s, Lemma.class);
            lemma.id = id;
            return lemma;
        });
        int total = dao.total();
        return new LemmaView(lemmata, total);
    }

    @RequestMapping(value = "/lemma", method = RequestMethod.DELETE)
    public void deleteLemma(
        @RequestParam(value = "id") int id)
    {
        dao.delete(id);
    }

    @RequestMapping(value = "/lemma", method = RequestMethod.POST)
    public void postLemma(
        @RequestBody String json)
    {
        Lemma lemma = gson.fromJson(json, Lemma.class);
        dao.insertNew(json, lemma.lemmaForm.text);
    }

    @ModelAttribute
    public void setVaryResponseHeader(HttpServletResponse response) {
        //bad practice, see CORS
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Origin", "*");
    }
}
