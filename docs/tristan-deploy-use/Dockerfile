FROM flink:1.12.0-scala_2.11-java8
ADD flink-streaming-platform-web.tar.gz /app/
RUN rm -rf /app/flink-streaming-platform-web.tar.gz
WORKDIR /app/flink-streaming-platform-web/lib
VOLUME /app/flink-streaming-platform-web/conf
VOLUME /app/flink-streaming-platform-web/logs
ENTRYPOINT ["java", "-jar", "flink-streaming-web-1.3.0.RELEASE.jar"]