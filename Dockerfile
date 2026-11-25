# Imagem para o Render (serviço web com Docker). Os testes rodam antes, no ./mvnw verify.
FROM eclipse-temurin:25-jdk AS construcao
WORKDIR /app
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -q -B dependency:go-offline
COPY config config
COPY src src
RUN ./mvnw -q -B -DskipTests -Ddependency-check.skip=true -Dspotless.check.skip=true -Dcheckstyle.skip=true package

FROM eclipse-temurin:25-jre
WORKDIR /app
RUN useradd --system --uid 10001 vaolivre
COPY --from=construcao /app/target/vao-livre.jar app.jar
USER vaolivre
ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
