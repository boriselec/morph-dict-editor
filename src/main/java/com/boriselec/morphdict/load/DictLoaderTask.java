package com.boriselec.morphdict.load;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DictLoaderTask {
    private final DictLoader dictLoader;

    public DictLoaderTask(DictLoader dictLoader) {
        this.dictLoader = dictLoader;
    }

    @Scheduled(fixedDelayString = "#{${dict.loader.delay.minutes} * 60 * 1000}")
    public void update() {
        dictLoader.ensureLastVersion();
    }
}
