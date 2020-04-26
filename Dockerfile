FROM jetty:9-jre11

COPY src /home/src
COPY pom.xml /home/

USER root
SHELL ["/bin/bash", "-c"]
RUN apt-get update \
    && apt-get -y install maven \
#    no jdk in base image
    && apt-get -y install openjdk-11-jdk \
    && echo "JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64/" >> /etc/environment \
    && source /etc/environment \
    && cd /home \
    && mvn compile \
    && mvn war:war \
    && cp /home/target/morph*war /var/lib/jetty/webapps/ \
    && rm -rfv /home/*
