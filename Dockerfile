# 第一階段：使用 Adoptium 完整路徑來編譯
FROM docker.io/library/eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# 第二階段：使用 Adoptium 完整路徑來執行
FROM docker.io/library/eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
