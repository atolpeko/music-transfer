FROM  amazoncorretto:17-alpine-jdk
ARG VERSION
COPY /target/service-config-api-${VERSION}.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
