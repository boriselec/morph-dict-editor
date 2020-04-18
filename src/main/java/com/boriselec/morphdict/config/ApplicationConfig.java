package com.boriselec.morphdict.config;

import com.boriselec.morphdict.Application;
import com.boriselec.morphdict.dom.data.Lemma;
import com.boriselec.morphdict.dom.edit.ChainLemmaTransformer;
import com.boriselec.morphdict.dom.edit.DigitLemmaFilter;
import com.boriselec.morphdict.dom.edit.LemmaTransformer;
import com.boriselec.morphdict.dom.out.JsonLemmaWriter;
import com.boriselec.morphdict.dom.out.LemmaWriter;
import com.boriselec.morphdict.dom.out.XmlLemmaWriter;
import com.boriselec.morphdict.link.DictionaryLink;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.nio.file.Paths;
import java.util.concurrent.locks.ReentrantLock;

@Configuration
@ComponentScan(basePackageClasses = Application.class)
@EnableScheduling
public class ApplicationConfig implements SchedulingConfigurer {
    @Bean
    public Jdbi jdbi(@Value("${MORPH_DB_URL}") String url,
                     @Value("${MORPH_DB_USERNAME}") String username,
                     @Value("${MORPH_DB_PASSWORD}") String password) {
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

    @Bean
    public JAXBContext lemmaJaxbContext() throws JAXBException {
        return JAXBContext.newInstance(Lemma.class);
    }

    @Bean
    public LemmaTransformer lemmaFilter() {
        return new ChainLemmaTransformer(
            new DigitLemmaFilter()
        );
    }

    @Bean
    @Order(value = 1)
    public DictionaryLink json(@Value("${MORPH_FILE_ROOT}") String fileRoot,
                               JAXBContext lemmaJaxbContext) {
        return new DictionaryLink("xml", Paths.get(fileRoot + "dict.opcorpora.filtered.xml")) {
            @Override
            public LemmaWriter getWriter() {
                try {
                    Marshaller marshaller = lemmaJaxbContext.createMarshaller();
                    marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
                    return new XmlLemmaWriter(marshaller, fileRoot + getPath().getFileName().toString());
                } catch (JAXBException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    @Bean
    @Order(value = 2)
    public DictionaryLink xml(@Value("${MORPH_FILE_ROOT}") String fileRoot,
                              @Qualifier("internal") Gson gson) {
        return new DictionaryLink("json", Paths.get(fileRoot + "dict.opcorpora.filtered.json")) {
            @Override
            public LemmaWriter getWriter() {
                return new JsonLemmaWriter(gson, fileRoot + getPath().getFileName().toString());
            }
        };
    }

    @Bean
    public ReentrantLock inFileLock() {
        return new ReentrantLock();
    }

    @Bean
    public ReentrantLock dbLock() {
        return new ReentrantLock();
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(3);
        taskScheduler.initialize();
        taskRegistrar.setTaskScheduler(taskScheduler);
    }
}
