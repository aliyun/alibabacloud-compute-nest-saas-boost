# 阶段1: 使用Maven官方镜像构建应用
FROM maven:3.6 AS builder

ADD ./ /workspace
# 复制项目文件
#ADD pom.xml .
#ADD /boost.common/pom.xml ./boost.common/
#ADD ./boost.common/src ./boost.common/src
#ADD ./boost.server/pom.xml ./boost.server/
#ADD ./boost.server/src ./boost.server/src
#ADD ./boost.serverless/pom.xml ./boost.serverless/
#ADD ./boost.serverless/src ./boost.serverless/src
#WORKDIR /workspace/alibabacloud-compute-nest-saas-boost
RUN find / -type f -name "AdapterManagerImpl.java"

RUN pwd && ls -la
WORKDIR /workspace

RUN pwd && ls -la

WORKDIR /

RUN pwd && ls -la

# 先安装common模块, 再构建Serverless应用，排除boost.server模块
RUN mvn clean package -DskipTests

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

