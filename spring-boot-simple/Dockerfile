FROM openjdk:8-jdk-alpine

ENV ELASTIC_APM_VERSION "1.7.0"
ENV ELASTIC_APM_SERVER "http://localhost:8200"

VOLUME /tmp
ARG JAR_FILE
COPY ${JAR_FILE} app.jar

RUN wget -O apm-agent.jar https://search.maven.org/remotecontent?filepath=co/elastic/apm/elastic-apm-agent/$ELASTIC_APM_VERSION/elastic-apm-agent-$ELASTIC_APM_VERSION.jar


CMD java -javaagent:/apm-agent.jar -Delastic.apm.server_url=$ELASTIC_APM_SERVER -Delastic.apm.service_name=spring-boot-sample -Djava.security.egd=file:/dev/./urandom -jar /app.jar