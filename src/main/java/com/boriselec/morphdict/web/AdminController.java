package com.boriselec.morphdict.web;

import com.boriselec.morphdict.dom.in.DatabaseDictLoader;
import com.boriselec.morphdict.link.FileDictRepository;
import com.boriselec.morphdict.load.DictLoader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController("/admin")
@RequestMapping("/admin")
public class AdminController {
    private final DictLoader dictLoader;
    private final FileDictRepository fileDictRepository;
    private final DatabaseDictLoader databaseDictLoader;

    public AdminController(DictLoader dictLoader,
                           FileDictRepository fileDictRepository,
                           DatabaseDictLoader databaseDictLoader) {
        this.dictLoader = dictLoader;
        this.fileDictRepository = fileDictRepository;
        this.databaseDictLoader = databaseDictLoader;
    }

    @RequestMapping(value = "/sync/dict/in", method = RequestMethod.POST)
    public void syncDict() {
        dictLoader.ensureLastVersion();
    }

    @RequestMapping(value = "/sync/db", method = RequestMethod.POST)
    public void syncDb() {
        databaseDictLoader.load();
    }

    @RequestMapping(value = "/sync/dict/out", method = RequestMethod.POST)
    public void writeDictJson() {
        fileDictRepository.update();
    }
}
