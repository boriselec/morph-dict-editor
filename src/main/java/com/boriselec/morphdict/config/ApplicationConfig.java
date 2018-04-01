package com.boriselec.morphdict.config;

import com.boriselec.morphdict.Application;
import com.boriselec.morphdict.dom.data.Lemma;
import com.boriselec.morphdict.dom.edit.ChainLemmaTransformer;
import com.boriselec.morphdict.dom.edit.DigitLemmaFilter;
import com.boriselec.morphdict.dom.edit.LemmaTransformer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Value;
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
    public Jdbi jdbi(@Value("${db.url}") String url,
                     @Value("${db.username}") String username,
                     @Value("${db.password}") String password) {
        return Jdbi.create(url, username, password);
    }

    @Bean
    public Gson internal() {
        return new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();
    }

    @Bean
    public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    //not thread-safe
    @Bean
    public Unmarshaller lemmaUnmarshaller() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Lemma.class);
        return jaxbContext.createUnmarshaller();
    }

    @Bean
    public LemmaTransformer lemmaFilter() {
        return new ChainLemmaTransformer(
            new DigitLemmaFilter()
        );
    }
}
