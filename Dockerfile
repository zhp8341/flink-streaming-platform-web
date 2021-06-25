FROM openjdk:8

WORKDIR /opt/flink

WORKDIR application

ENV MYSQL_HOST="127.0.0.1" \
    MYSQL_PORT="3306" \
    MYSQL_USERNAME="root" \
    MYSQL_PASSWORD="root" \
    SERVER_PORT=8080 \
    LOG_HOME="/application/logs" \
    JVM_OPTS="" \
    SPRING_ENV="prod"

COPY flink-streaming-core/target/flink-streaming-core.jar /application/lib/flink-streaming-core.jar

COPY flink-streaming-web/target/flink-streaming-web.jar /application/flink-streaming-web.jar

CMD java $JAVA_OPTS \
    -jar /application/flink-streaming-web.jar \
    --spring.profiles.active=${SPRING_ENV} --server.port=${SERVER_PORT}


