FROM openjdk:8-jdk-alpine

ENV ELASTIC_APM_VERSION "1.8.0"
ENV ELASTIC_APM_SERVER "http://localhost:8200"

COPY target/spring-boot-simple.jar /app.jar

RUN wget -O /apm-agent.jar https://search.maven.org/remotecontent?filepath=co/elastic/apm/elastic-apm-agent/$ELASTIC_APM_VERSION/elastic-apm-agent-$ELASTIC_APM_VERSION.jar

CMD java -jar /app.jar
