FROM alpine:latest

ARG file_name=discord-bot-1.0.0.jar
ENV target_name=app.jar
RUN apk add --no-cache openjdk17-jre-headless
COPY target/$file_name $target_name

ENTRYPOINT java -jar $target_name