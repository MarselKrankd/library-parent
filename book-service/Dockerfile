# Образ микросервиса book-service. Многоступенчатая сборка, контекст — корень проекта:
#   docker build -f book-service/Dockerfile -t girichev/book-service:1.0.0 .

# 1) builder: официальный образ Maven с JDK 21 собирает модуль вместе с корневым pom.xml
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /workspace
COPY pom.xml .
COPY book-service/pom.xml book-service/
COPY author-service/pom.xml author-service/
# сначала только зависимости — этот слой кэшируется, пока не меняются pom.xml
RUN mvn -B -q -pl book-service -am dependency:go-offline
COPY book-service/src book-service/src
RUN mvn -B -q -pl book-service -am package -DskipTests

# 2) extractor: jar раскладывается на сам код (book-service.jar) и каталог lib/ с библиотеками
FROM eclipse-temurin:21-jdk AS extractor
WORKDIR /extract
COPY --from=builder /workspace/book-service/target/book-service.jar .
RUN java -Djarmode=tools -jar book-service.jar extract --destination app

# 3) runtime: только JRE и разложенное приложение
FROM eclipse-temurin:21-jre
WORKDIR /opt/book-service
COPY --from=extractor /extract/app/lib ./lib
COPY --from=extractor /extract/app/book-service.jar .
EXPOSE 8070
# Docker сам следит за состоянием сервиса через Spring Boot Actuator
HEALTHCHECK --interval=10s --timeout=3s --start-period=30s --retries=5 \
  CMD curl -fs http://localhost:8070/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "book-service.jar"]
