FROM  amazoncorretto:17-alpine-jdk
ARG VERSION
COPY /target/transfer-api-${VERSION}.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
