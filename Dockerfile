FROM dh-iutl.univ-artois.fr/gradle:jdk21-alpine AS BUILD
WORKDIR /usr/app/
COPY . .
RUN gradle build -x test

FROM dh-iutl.univ-artois.fr/openjdk:21
ENV JAR_NAME=picit-api-1.5.0.jar
ENV APP_HOME=/usr/app
WORKDIR $APP_HOME
COPY --from=BUILD $APP_HOME/build/libs/$JAR_NAME .
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "picit-api-1.5.0.jar"]