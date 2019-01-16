FROM openjdk:8-slim

ENV NAME=transactions
ENV BUCKET=undefined
ENV BUCKET_KEY=undefined
ENV BUCKET_SECRET=undefined
ENV IP=127.0.0.1
ENV JAR=$NAME-1.0.jar

WORKDIR $NAME

ADD target/$JAR .
ADD target/lib lib
ADD src/main/resources/default-jgroups-tcp.xml  .
ADD src/main/resources/default-jgroups-google.xml  .
ADD src/main/bin/run.sh  .
RUN chmod +x .

CMD ["bash", "/transactions/run.sh"]
