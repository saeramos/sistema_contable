# Multi-stage build para optimizar el tamaño de la imagen
FROM eclipse-temurin:17-jdk-alpine AS builder

# Instalar dependencias del sistema
RUN apk add --no-cache curl

WORKDIR /app

# Copiar archivos de configuración de Maven
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Hacer ejecutable mvnw
RUN chmod +x mvnw

# Descargar dependencias (capa separada para cache)
RUN ./mvnw dependency:go-offline -B

# Copiar código fuente
COPY src src

# Construir la aplicación
RUN ./mvnw clean package -DskipTests -B

# Etapa de runtime
FROM eclipse-temurin:17-jre-alpine

# Instalar dependencias del sistema para runtime
RUN apk add --no-cache curl tzdata

# Crear usuario no-root para seguridad
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

WORKDIR /app

# Copiar el JAR construido
COPY --from=builder /app/target/sistema-contabilidad-1.0.0.jar app.jar

# Cambiar propiedad de archivos al usuario no-root
RUN chown -R appuser:appgroup /app

# Cambiar al usuario no-root
USER appuser

# Configurar variables de entorno para JVM
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:+UseContainerSupport"

# Exponer puerto
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Comando de inicio
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"] 