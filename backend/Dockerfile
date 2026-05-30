# Single image containing every runnable service jar; each container picks one
# via the SERVICE env var. Built once, reused by all backend compose services.
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY . .
RUN mvn -q -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /build/*/target/*-exec.jar /app/
RUN for f in /app/*-exec.jar; do mv "$f" "$(echo "$f" | sed 's/-0.0.1-SNAPSHOT-exec//')"; done
ENV SERVICE=api-gateway
ENTRYPOINT ["sh", "-c", "java -jar /app/${SERVICE}.jar"]
