FROM maven:3.6 AS builder
WORKDIR /workspace

ADD . /workspace

RUN mvn clean package -pl '!boost.server' -DskipTests

# 阶段2: 使用Java官方镜像运行构建的应用
FROM openjdk:8-jre-slim

# 设置工作目录
WORKDIR /app

# 从构建阶段复制构建好的jar包到工作目录
COPY --from=builder /workspace/boost.serverless/target/boost.serverless-1.0.0-SNAPSHOT.jar .

# 暴露应用端口
EXPOSE 9000

# 定义容器启动时执行的命令
CMD ["java", "-jar", "boost.serverless-1.0.0-SNAPSHOT.jar"]

