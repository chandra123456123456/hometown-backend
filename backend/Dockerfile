# Lightweight runtime image: packages the jars already built on the host
# (run `mvn -DskipTests package` first). Avoids a heavy in-container Maven build.
# Each container selects its jar via the SERVICE env var.
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY */target/*-exec.jar /app/
RUN for f in /app/*-exec.jar; do mv "$f" "$(echo "$f" | sed 's/-0.0.1-SNAPSHOT-exec//')"; done
ENV SERVICE=api-gateway
ENTRYPOINT ["sh", "-c", "java -jar /app/${SERVICE}.jar"]
