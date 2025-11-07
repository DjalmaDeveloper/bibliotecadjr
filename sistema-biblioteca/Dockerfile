# ==================================
# ESTÁGIO 1: BUILD
# ==================================
FROM maven:3.9.5-eclipse-temurin-17-alpine AS build

# Definir diretório de trabalho
WORKDIR /app

# Copiar arquivos de dependências primeiro (para cache de camadas)
COPY pom.xml .
COPY src ./src

# Baixar dependências e fazer build
RUN mvn clean package -DskipTests

# ==================================
# ESTÁGIO 2: RUNTIME
# ==================================
FROM eclipse-temurin:17-jre-alpine

# Metadados da imagem
LABEL maintainer="djalma.ant@gmail.com"
LABEL description="Sistema de Biblioteca - Backend Spring Boot"
LABEL version="1.0"

# Criar usuário não-root para segurança
RUN addgroup -S spring && adduser -S spring -G spring

# Definir diretório de trabalho
WORKDIR /app

# Copiar JAR do estágio de build
COPY --from=build /app/target/*.jar app.jar

# Mudar proprietário do arquivo
RUN chown spring:spring app.jar

# Mudar para usuário não-root
USER spring:spring

# Expor porta (Render usa variável PORT)
EXPOSE 8080

# Variáveis de ambiente padrão
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseContainerSupport"
ENV SPRING_PROFILES_ACTIVE=prod

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:${PORT:-8080}/api/autores || exit 1

# Comando de inicialização
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8080} -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} -jar app.jar"]