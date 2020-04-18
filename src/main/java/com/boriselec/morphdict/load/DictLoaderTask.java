package com.boriselec.morphdict.load;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DictLoaderTask {
    private final DictLoader dictLoader;

    public DictLoaderTask(DictLoader dictLoader) {
        this.dictLoader = dictLoader;
    }

    @Scheduled(fixedDelayString = "#{${MORPH_FILELOADER_PERIOD_MINUTES} * 60 * 1000}")
    public void update() {
        dictLoader.ensureLastVersion();
    }
}
