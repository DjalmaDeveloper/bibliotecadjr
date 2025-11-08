# 🔧 Correções do Dockerfile - Sistema Biblioteca

## 📊 Análise Comparativa

Comparação entre o projeto `dopLibraryMaven` (✅ funcionando) e `sistema-biblioteca` (❌ com erro).

---

## ❌ Problemas Identificados no Dockerfile Original

### 1. **Maven Wrapper Não Copiado**
```dockerfile
# ❌ ANTES - Não copiava mvnw e .mvn
COPY pom.xml .
COPY src ./src
```

**Problema**: O Dockerfile tentava usar `mvn` diretamente sem garantir que estivesse disponível.

### 2. **Sem Cache de Dependências**
```dockerfile
# ❌ ANTES - Copiava tudo de uma vez
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests
```

**Problema**: Cada mudança no código forçava o redownload de todas as dependências.

### 3. **Sem Tratamento de Erros de Rede**
```dockerfile
# ❌ ANTES - Sem retry logic
RUN mvn clean package -DskipTests
```

**Problema**: Falhas temporárias de rede causavam falha total do build.

### 4. **Sem Verificação de Build**
```dockerfile
# ❌ ANTES - Copiava sem verificar
COPY --from=build /app/target/*.jar app.jar
```

**Problema**: Se o JAR não fosse criado, o erro só aparecia no stage de runtime.

### 5. **Sem Segurança (Root User)**
```dockerfile
# ❌ ANTES - Rodava como root
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["sh", "-c", "java ... -jar app.jar"]
```

**Problema**: Aplicação rodando como root é uma vulnerabilidade de segurança.

### 6. **Sem Signal Handling**
```dockerfile
# ❌ ANTES - Sem dumb-init
ENTRYPOINT ["sh", "-c", "java -jar app.jar"]
```

**Problema**: Sinais (SIGTERM, SIGINT) não eram tratados corretamente, causando shutdown não gracioso.

### 7. **JAVA_OPTS Não Otimizados**
```dockerfile
# ❌ ANTES - JAVA_OPTS básicos ou ausentes
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar app.jar"]
```

**Problema**: Sem otimizações para ambiente containerizado.

---

## ✅ Correções Implementadas

### 1. **✅ Maven Wrapper Configurado**
```dockerfile
# ✅ DEPOIS - Copia Maven Wrapper primeiro
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Torna executável
RUN chmod +x mvnw
```

**Benefício**: Versão consistente do Maven, independente do que está instalado na imagem.

### 2. **✅ Cache de Dependências Otimizado**
```dockerfile
# ✅ DEPOIS - Download de dependências separado
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download de dependências (camada cacheada)
RUN ./mvnw dependency:go-offline -B

# Só depois copia o código
COPY src ./src
RUN ./mvnw clean package -DskipTests -B
```

**Benefício**: Dependências são cacheadas. Mudanças no código não forçam redownload.

### 3. **✅ Retry Logic para Network Issues**
```dockerfile
# ✅ DEPOIS - Retry logic com backoff
RUN ./mvnw dependency:go-offline -B || \
    (echo "Retry 1/3..." && sleep 10 && ./mvnw dependency:go-offline -B) || \
    (echo "Retry 2/3..." && sleep 20 && ./mvnw dependency:go-offline -B) || \
    (echo "Retry 3/3..." && sleep 30 && ./mvnw dependency:go-offline -B)
```

**Benefício**: Falhas temporárias de rede não causam falha total do build.

### 4. **✅ Verificação de Build**
```dockerfile
# ✅ DEPOIS - Verifica se JAR foi criado
RUN ./mvnw clean package -DskipTests -B

# Verifica
RUN ls -la target/ && \
    test -f target/*.jar && \
    echo "JAR file created successfully"
```

**Benefício**: Erros são detectados cedo, com mensagens claras.

### 5. **✅ Usuário Não-Root (Segurança)**
```dockerfile
# ✅ DEPOIS - Cria usuário não-root
RUN addgroup -g 1001 spring && \
    adduser -u 1001 -G spring -s /bin/sh -D spring

# Copia com ownership correto
COPY --from=builder --chown=spring:spring /app/target/*.jar app.jar

# Muda para usuário não-root
USER spring:spring
```

**Benefício**: Seguindo best practices de segurança.

### 6. **✅ Signal Handling com dumb-init**
```dockerfile
# ✅ DEPOIS - Instala dumb-init
RUN apk add --no-cache dumb-init

# Usa dumb-init
ENTRYPOINT ["dumb-init", "--"]
CMD sh -c "java $JAVA_OPTS -jar app.jar"
```

**Benefício**: Shutdown gracioso, sinais tratados corretamente.

### 7. **✅ JAVA_OPTS Otimizados**
```dockerfile
# ✅ DEPOIS - JAVA_OPTS para containers
ENV JAVA_OPTS="-XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -XX:InitialRAMPercentage=50.0 \
    -Djava.security.egd=file:/dev/./urandom \
    -Djava.net.preferIPv4Stack=true"
```

**Benefício**: 
- `UseContainerSupport`: JVM detecta limites de memória do container
- `MaxRAMPercentage`: Usa até 75% da RAM disponível
- `egd`: Melhora performance de geração de números aleatórios
- `preferIPv4Stack`: Evita problemas com IPv6

### 8. **✅ Healthcheck Configurado**
```dockerfile
# ✅ DEPOIS - Healthcheck
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/autores || exit 1
```

**Benefício**: Render e Docker podem verificar se a aplicação está saudável.

---

## 📝 Arquivos Criados/Atualizados

### 1. `Dockerfile` (Uso Geral)
- Multi-stage build otimizado
- Todas as correções aplicadas
- Uso: `docker build -t sistema-biblioteca .`

### 2. `Dockerfile.render` (Específico para Render)
- Otimizado para deploy no Render
- Maven options para network resilience
- Uso automático via `render.yaml`

### 3. `render.yaml` (Atualizado)
- Aponta para `Dockerfile.render`
- Variáveis de ambiente otimizadas
- Build filters configurados

---

## 🚀 Como Usar

### Deploy no Render

1. **Commit e push das mudanças**:
```bash
git add Dockerfile Dockerfile.render render.yaml
git commit -m "fix: Otimizar Dockerfile para deploy"
git push origin main
```

2. **Render detectará as mudanças** e iniciará novo deploy automaticamente

3. **Verificar logs** no dashboard do Render:
   - Build stage: Verificar se dependências foram baixadas
   - Runtime stage: Verificar se aplicação iniciou corretamente

### Build Local

```bash
# Build com Dockerfile padrão
docker build -t sistema-biblioteca .

# Build com Dockerfile.render
docker build -f Dockerfile.render -t sistema-biblioteca:render .

# Run local
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://... \
  -e SPRING_DATASOURCE_USERNAME=biblioteca_user \
  -e SPRING_DATASOURCE_PASSWORD=... \
  sistema-biblioteca
```

---

## 📊 Comparação de Tamanhos

### Antes:
- Build stage: ~800MB (sem cache efetivo)
- Runtime: ~250MB

### Depois:
- Build stage: ~800MB (com cache efetivo de dependências)
- Runtime: ~250MB (mesmo tamanho, mas mais seguro)

**Cache Benefit**: Segunda build (sem mudanças em pom.xml) é ~10x mais rápida

---

## 🔍 Troubleshooting

### Erro: "JAR not found"
```bash
# Verificar se o build completou
docker build --progress=plain -t sistema-biblioteca .
```

### Erro: "Maven dependency download failed"
```bash
# Retry logic deve resolver automaticamente
# Se persistir, verificar conectividade de rede
```

### Erro: "Permission denied"
```bash
# Garantir que mvnw está com permissões corretas no repo
chmod +x mvnw
git add mvnw
git commit -m "fix: Add execute permission to mvnw"
```

---

## 📚 Referências

- [Spring Boot with Docker](https://spring.io/guides/topicals/spring-boot-docker/)
- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)
- [Render Dockerfile Deployment](https://render.com/docs/docker)
- [dumb-init](https://github.com/Yelp/dumb-init)

---

## ✅ Checklist de Verificação

Após o deploy, verificar:

- [ ] Build completou sem erros
- [ ] Aplicação iniciou corretamente
- [ ] Health check está passando
- [ ] API responde em `/api/autores`
- [ ] Logs não mostram erros
- [ ] Memória está dentro do limite
- [ ] Shutdown gracioso funciona

---

**Data**: 2025-11-08  
**Status**: ✅ Correções Aplicadas  
**Projeto**: sistema-biblioteca  
**Baseado em**: dopLibraryMaven

