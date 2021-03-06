package com.boriselec.morphdict.web;

import com.boriselec.morphdict.dom.data.Lemma;
import com.boriselec.morphdict.storage.sql.LemmaDao;
import com.boriselec.morphdict.web.view.LemmaView;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("/api/lemma")
@RequestMapping("/api/lemma")
public class LemmaController {
    private final LemmaDao dao;

    public LemmaController(LemmaDao dao) {
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
    @CrossOrigin
    @RequestMapping(params = {"offset", "limit"}, method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public LemmaView getLemma(
        @RequestParam(value = "offset") int offset,
        @RequestParam(value = "limit") int limit)
    {
        List<Lemma> lemmata = dao.get(offset, limit);
        int total = dao.total();
        return new LemmaView(lemmata, total);
    }

    @CrossOrigin
    @RequestMapping(params = {"text", "offset", "limit"}, method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public LemmaView searchLemma(
        @RequestParam(value = "text") String text,
        @RequestParam(value = "offset") int offset,
        @RequestParam(value = "limit") int limit)
    {
        List<Lemma> lemmata = dao.search(text, offset, limit);
        int total = dao.totalSearch(text);
        return new LemmaView(lemmata, total);
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.DELETE)
    public void deleteLemma(
        @RequestParam(value = "id") int id)
    {
        dao.markDeleted(id);
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.TEXT_PLAIN_VALUE)
    public void postLemma(
        @RequestBody String json)
    {
        dao.insertNew(json);
    }
}
