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
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.util.concurrent.locks.ReentrantLock;

@Configuration
@ComponentScan(basePackageClasses = Application.class)
@PropertySource(value = "classpath:application.properties")
@EnableScheduling
public class ApplicationConfig implements SchedulingConfigurer {
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
    public DictionaryLink json(@Value("#{'${static.url}' + '${xml.path}'}") String xmlPath,
                               JAXBContext lemmaJaxbContext) {
        return new DictionaryLink("xml", xmlPath) {
            @Override
            public LemmaWriter getWriter() {
                try {
                    Marshaller marshaller = lemmaJaxbContext.createMarshaller();
                    marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
                    return new XmlLemmaWriter(marshaller, xmlPath);
                } catch (JAXBException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    @Bean
    public DictionaryLink xml(@Value("#{'${static.url}' + '${json.path}'}") String jsonPath,
                              @Qualifier("internal") Gson gson) {
        return new DictionaryLink("json", jsonPath) {
            @Override
            public LemmaWriter getWriter() {
                return new JsonLemmaWriter(gson, jsonPath);
            }
        };
    }

    @Bean
    public ReentrantLock inFileLock() {
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
