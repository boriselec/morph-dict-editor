package com.boriselec.morphdict.config;

import com.boriselec.morphdict.Application;
import com.boriselec.morphdict.dom.data.LemmaViewExclusionStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jdbi.v3.core.Jdbi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = Application.class)
public class ApplicationConfig {
    @Bean
    public Jdbi jdbi() {
        return Jdbi.create("jdbc:mysql://localhost:3306/dict?user=root&password=admin");
    }

    @Bean
    Gson gson() {
        return new GsonBuilder()
            .setExclusionStrategies(new LemmaViewExclusionStrategy())
            .create();
    }
}
