package com.boriselec.morphdict.web;

import com.boriselec.morphdict.link.DictionaryLink;
import com.boriselec.morphdict.link.FileDictRepository;
import com.boriselec.morphdict.storage.sql.LemmaDao;
import com.boriselec.morphdict.web.view.LinksView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

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
    public void getLink(@PathVariable String description, HttpServletResponse response) throws IOException {
        Optional<Path> link = repository.getLinkByDescription(description)
            .map(DictionaryLink::getPath);
        if (link.isPresent()) {
            try {
                String filename = link.get().getFileName().toString();

                InputStream is = new FileInputStream(link.get().toString());

                response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

                int read;
                byte[] bytes = new byte[1024];
                OutputStream os = response.getOutputStream();

                while ((read = is.read(bytes)) != -1) {
                    os.write(bytes, 0, read);
                }
                os.flush();
                os.close();

            } catch (IOException e) {
                log.error(e.getMessage(), e);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().print(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().print(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
