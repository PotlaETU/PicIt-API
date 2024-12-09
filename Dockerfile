FROM gradle:jdk21-alpine AS BUILD
WORKDIR /usr/app/
COPY . .
RUN gradle build -x test

FROM openjdk:21
ENV JAR_NAME=picit-api-1.3.1.jar
ENV APP_HOME=/usr/app
WORKDIR $APP_HOME
COPY --from=BUILD $APP_HOME/build/libs/$JAR_NAME .
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "picit-api-1.3.1.jar"]