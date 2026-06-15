# 使用 maven 官方映像檔，它已經內建了 mvn 指令
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
# 改用全路徑執行 mvn，避免路徑解析錯誤
RUN /usr/bin/mvn clean package -DskipTests

# 第二階段：執行
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
# 將編譯好的 JAR 複製出來
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
