FROM eclipse-temurin:17.0.17_10-jre

ENV NAME=transactions
ENV CASSANDRA_HOST=cassandra
ENV CASSANDRA_PORT=9042
ENV COUNTRY_CODE=FR
ENV JAR=$NAME-1.0.jar

WORKDIR $NAME

ADD target/$JAR .
ADD target/lib lib
ADD src/main/bin/run.sh  .
RUN chmod +x .

CMD ["/bin/sh", "/transactions/run.sh"]
