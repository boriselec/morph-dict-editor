package com.boriselec.morphdict.web;

import com.boriselec.morphdict.dom.data.Lemma;
import com.boriselec.morphdict.storage.sql.LemmaDao;
import com.boriselec.morphdict.web.view.LemmaView;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController("/api")
@RequestMapping("/api")
public class ApiController {
    private final LemmaDao dao;

    public ApiController(LemmaDao dao) {
        this.dao = dao;
    }

    /**
     * {"meta": {
     *  "total": 200
     * },
     * "lemmata": [{
     *  "l":{"t":"ёж","g":[...]},
     *  "f":[...],
     *  "id":1,
     *  "state":"0"
     * }]
     */
    @RequestMapping(value = "/lemma", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public LemmaView getLemma(
        @RequestParam(value = "offset") int offset,
        @RequestParam(value = "limit") int limit)
    {
        List<Lemma> lemmata = dao.get(offset, limit);
        int total = dao.total();
        return new LemmaView(lemmata, total);
    }

    @RequestMapping(value = "/lemma", method = RequestMethod.DELETE)
    public void deleteLemma(
        @RequestParam(value = "id") int id)
    {
        dao.delete(id);
    }

    @RequestMapping(value = "/lemma", method = RequestMethod.POST, consumes = MediaType.TEXT_PLAIN_VALUE)
    public void postLemma(
        @RequestBody String json)
    {
        dao.insertNew(json);
    }

    @ModelAttribute
    public void setVaryResponseHeader(HttpServletResponse response) {
        //bad practice, see CORS
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Origin", "*");
    }
}
