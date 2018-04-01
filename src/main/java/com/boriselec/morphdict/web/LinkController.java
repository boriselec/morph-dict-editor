package com.boriselec.morphdict.web;

import com.boriselec.morphdict.storage.sql.LemmaDao;
import com.boriselec.morphdict.web.view.LinkView;
import com.boriselec.morphdict.web.view.LinksView;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@RestController("/api/link")
@RequestMapping("/api/link")
public class LinkController {
    private final String jsonPath;
    private final String xmlPath;
    private final LemmaDao lemmaDao;

    public LinkController(@Value("${json.path}") String jsonPath,
                          @Value("${xml.path}") String xmlPath,
                          LemmaDao lemmaDao) {
        this.jsonPath = jsonPath;
        this.xmlPath = xmlPath;
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
        List<LinkView> links = Arrays.asList(
            new LinkView("json", checkUrl(jsonPath), null),
            new LinkView("xml", checkUrl(xmlPath), null)
        );
        int revision = lemmaDao.getDictionaryRevision();
        return new LinksView(links, revision);
    }

    private String checkUrl(String path) {
        return Files.exists(Paths.get(path)) ? path : null;
    }
}
