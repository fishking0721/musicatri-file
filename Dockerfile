# 使用官方Java基础镜像
FROM eclipse-temurin:17-jdk-jammy

# 设置工作目录
WORKDIR /app

# 复制构建产物
COPY target/*.jar app.jar

# 暴露端口
EXPOSE 8080

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]