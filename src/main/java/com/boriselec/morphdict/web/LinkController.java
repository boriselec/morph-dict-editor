package com.boriselec.morphdict.web;

import com.boriselec.morphdict.link.DictionaryLink;
import com.boriselec.morphdict.link.FileDictRepository;
import com.boriselec.morphdict.storage.sql.LemmaDao;
import com.boriselec.morphdict.web.view.LinksView;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RestController("/api/link")
@RequestMapping("/api/link")
public class LinkController {
    private final FileDictRepository repository;
    private final LemmaDao lemmaDao;

    public LinkController(FileDictRepository repository,
                          LemmaDao lemmaDao) {
        this.repository = repository;
        this.lemmaDao = lemmaDao;
    }

    /**
     * {"meta": {
     *   "revision": 1410
     * },
     * "link": [{
     *   "name": "json dict",
     *   "url": "~/dict"
     *  },{
     *   "name": "xml dict"
     *  }]
     * }
     *
     */
    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public LinksView getLinks() {
        List<DictionaryLink> links = repository.getLinks();
        int revision = lemmaDao.getDictionaryRevision();
        return new LinksView(links, revision);
    }

    private String checkUrl(String path) {
        return Files.exists(Paths.get(path)) ? path : null;
    }
}
