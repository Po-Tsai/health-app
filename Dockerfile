# 使用輕量級的 Java 17 開發環境
FROM eclipse-temurin:17-jdk-jammy

# 設定工作目錄
WORKDIR /app

# 複製所有檔案並編譯
COPY . .
RUN ./mvnw clean package -DskipTests

# 啟動應用程式
CMD ["java", "-jar", "target/*.jar"]