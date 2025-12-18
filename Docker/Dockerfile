# Используем легковесный JRE для рантайма
FROM eclipse-temurin:17-jre-alpine

# Оптимизация 1: Не-root пользователь для безопасности
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

WORKDIR /app

# Оптимизация 2: Копируем только JAR.
# Если у вас Maven, артефакт лежит в target/, но по условию мы берем из build/libs/
COPY build/libs/*.jar app.jar

# Оптимизация 3: Healthcheck для мониторинга состояния
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD wget -q --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]