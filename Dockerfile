FROM jboss/wildfly:19.0.0.Final

COPY src /home/src
COPY pom.xml /home/

USER root
RUN yum -y install maven-3.0.5 \
    && yum clean all \
    && cd /home \
    && mvn compile \
    && mvn war:war \
    && cp /home/target/morph*war /opt/jboss/wildfly/standalone/deployments/morph.war \
    && rm -rfv /home/*
USER jboss
