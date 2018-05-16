FROM openjdk:8-jdk-alpine

LABEL maintainer="wagnerjfr@yahoo.com.br"

COPY . /home/app/

WORKDIR /home/app/src/

RUN javac com/example/server/*.java

CMD java -Dcom.sun.management.jmxremote.port=10000 \
  -Dcom.sun.management.jmxremote.authenticate=false \
  -Dcom.sun.management.jmxremote.ssl=false \
  com.example.server.ServerMain