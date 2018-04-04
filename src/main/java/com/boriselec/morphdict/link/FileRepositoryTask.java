package com.boriselec.morphdict.link;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class FileRepositoryTask {
    private final FileDictRepository fileDictRepository;

    public FileRepositoryTask(FileDictRepository fileDictRepository) {
        this.fileDictRepository = fileDictRepository;
    }

    @Scheduled(fixedDelayString = "#{${file.repository.delay.minutes} * 60 * 1000}")
    public void update() {
        fileDictRepository.update();
    }
}
