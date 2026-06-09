FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /backend
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN ./mvnw dependency:go-offline -B

# skip frontend
COPY src/main/java src/main/java
COPY src/main/resources src/main/resources
RUN ./mvnw package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /backend

ARG USER_ID=1000
ARG GROUP_ID=1000

RUN addgroup -g ${GROUP_ID} mbgroup && \
    adduser -u ${USER_ID} -G mbgroup -s /bin/sh -D mbuser

COPY --from=builder /backend/target/*.jar mb.jar

RUN mkdir -p /backend/data && \
    chown -R mbuser:mbgroup /backend

USER mbuser

ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+UseContainerSupport"
EXPOSE 8000

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar mb.jar"]
