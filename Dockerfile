FROM maven:3.9.6-amazoncorretto-17 as builder

WORKDIR /app

ADD pom.xml /app

RUN ["/usr/local/bin/mvn-entrypoint.sh", "mvn", "verify", "clean", "--fail-never"]

ADD . /app

RUN mvn -DskipTests package

# https://stackoverflow.com/questions/42208442/maven-docker-cache-dependencies

FROM amazoncorretto:17-alpine3.17

MAINTAINER "chuuuevi <chuuuevi@gmail.com>"

ARG TIME_ZONE=Asia/Shanghai
ARG APK_REPOSITORIES=mirrors.aliyun.com

RUN sed -i "s/dl-cdn.alpinelinux.org/${APK_REPOSITORIES}/g" /etc/apk/repositories

RUN apk add --update --no-cache tzdata git \
    && echo "${TIME_ZONE}" > /etc/timezone \
    && ln -sf /usr/share/zoneinfo/${TIME_ZONE} /etc/localtime \
    && rm -rf /var/cache/apk/*

VOLUME /tmp

WORKDIR /app

COPY --from=builder /app/target/*.jar git-stat.jar

EXPOSE 8080

ENTRYPOINT exec java $JAVA_OPTS -jar /app/git-stat.jar
