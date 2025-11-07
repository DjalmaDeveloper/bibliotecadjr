# ==================================
# ESTÁGIO 1: BUILD
# ==================================
FROM maven:3.9.5-eclipse-temurin-17-alpine AS build

WORKDIR /app

# Copiar pom.xml e src
COPY pom.xml .
COPY src ./src

# Build da aplicação
RUN mvn clean package -DskipTests

# ==================================
# ESTÁGIO 2: RUNTIME
# ==================================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copiar JAR do estágio anterior
COPY --from=build /app/target/*.jar app.jar

# Expor porta
EXPOSE 8080

# Comando de inicialização
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8080} -Dspring.profiles.active=prod -jar app.jar"]