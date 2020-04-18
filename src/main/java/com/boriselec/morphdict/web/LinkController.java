package com.boriselec.morphdict.web;

import com.boriselec.morphdict.link.DictionaryLink;
import com.boriselec.morphdict.link.FileDictRepository;
import com.boriselec.morphdict.storage.sql.LemmaDao;
import com.boriselec.morphdict.web.view.LinksView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@RestController("/api/link")
@RequestMapping("/api/link")
public class LinkController {
    private static final Logger log = LoggerFactory.getLogger(LinkController.class);

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

    @CrossOrigin
    @RequestMapping(value = "/{description}", method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> getLink(@PathVariable String description) {
        return repository.getLinkByDescription(description)
            .map(DictionaryLink::getPath)
            .map(path -> {
                try {
                    String filename = path.getFileName().toString();

                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                    headers.setContentDispositionFormData(filename, filename);
                    headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
                    return new ResponseEntity<>(Files.readAllBytes(path), headers, HttpStatus.OK);
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                    return new ResponseEntity<byte[]>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
