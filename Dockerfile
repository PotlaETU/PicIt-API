FROM gradle:jdk21-alpine AS BUILD
WORKDIR /usr/app/
COPY . .
RUN gradle build -x test

FROM openjdk:21
ENV JAR_NAME=picit-api-1.2.0.jar
ENV APP_HOME=/usr/app
WORKDIR $APP_HOME
COPY --from=BUILD $APP_HOME .
EXPOSE 8081
ENTRYPOINT exec java -jar $APP_HOME/build/libs/$JAR_NAME