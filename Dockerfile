# build environment
FROM maven:3.6.3-jdk-11 as build
COPY src /home/src
COPY pom.xml /home/
RUN cd /home && \
    mvn compile && \
    mvn war:war

# production environment
FROM jetty:9-jre11
COPY --from=build /home/target/morph*war /var/lib/jetty/webapps/morph.war
