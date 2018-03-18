package com.boriselec.morphdict.config;

import com.boriselec.morphdict.Application;
import com.boriselec.morphdict.dom.data.Lemma;
import com.boriselec.morphdict.dom.data.LemmaViewExclusionStrategy;
import com.boriselec.morphdict.dom.out.DatabaseLemmaWriter;
import com.boriselec.morphdict.dom.out.LemmaWriter;
import com.boriselec.morphdict.storage.sql.LemmaDao;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jdbi.v3.core.Jdbi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

@Configuration
@ComponentScan(basePackageClasses = Application.class)
@PropertySource(value = "classpath:application.properties")
public class ApplicationConfig {
    @Bean
    public Jdbi jdbi() {
        return Jdbi.create("jdbc:mysql://localhost:3306/dict?user=root&password=admin");
    }

    @Bean
    public Gson gson() {
        return new GsonBuilder()
            .setExclusionStrategies(new LemmaViewExclusionStrategy())
            .create();
    }

    @Bean
    public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public Unmarshaller lemmaUnmarshaller() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Lemma.class);
        return jaxbContext.createUnmarshaller();
    }

    @Bean
    public LemmaDao lemmaDao(Jdbi jdbi) {
        return new LemmaDao(jdbi);
    }

    @Bean
    public LemmaWriter dbLemmaWriter(LemmaDao lemmaDao, Gson gson) {
        return new DatabaseLemmaWriter(lemmaDao, gson);
    }
}
