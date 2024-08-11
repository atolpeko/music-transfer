FROM  amazoncorretto:17-alpine-jdk
ARG VERSION
COPY /target/auth-api-${VERSION}.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
