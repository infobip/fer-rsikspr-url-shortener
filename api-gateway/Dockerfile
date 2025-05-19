# Stage 1, builds maven project in a large JDK based image
FROM maven:3.9-eclipse-temurin-21-alpine AS build-stage
WORKDIR /opt/api-gateway
COPY pom.xml .
RUN mvn dependency:go-offline
COPY ./src ./src
RUN mvn clean package -Dmaven.test.skip=true

# Stage 2, creates a JRE based image with compiled app
FROM eclipse-temurin:21-jre-alpine
WORKDIR /opt/api-gateway
COPY --from=build-stage /opt/api-gateway/target/api-gateway.jar ./app.jar
ENTRYPOINT ["java","-Xmx400M","-jar","app.jar"]