# 前端构建阶段
FROM node:lts-alpine AS frontend-builder

# 设置前端项目的工作目录
WORKDIR /app/boost.front

# 复制前端项目文件
COPY ./boost.front/package*.json ./

RUN cnpm install


# 复制其它前端源码文件
COPY ./boost.front .

# 构建前端项目
RUN npm run build

# 后端构建阶段
FROM maven:3.6.3 AS backend-builder

# 指定构建工作目录
WORKDIR /build

# 从前端构建阶段复制已构建的静态资源到后端项目中的静态资源目录
COPY --from=frontend-builder /app/boost.front/dist /app/boost.server/src/main/resources/static

WORKDIR /build

# 复制项目文件
COPY ./pom.xml .
COPY ./boost.common/pom.xml ./boost.common/
COPY ./boost.common/src ./boost.common/src
COPY ./boost.server/pom.xml ./boost.server/
COPY ./boost.server/src ./boost.server/src
COPY ./boost.serverless/pom.xml ./boost.serverless/
COPY ./boost.serverless/src ./boost.serverless/src

# 先安装common模块, 再构建Serverless应用，排除boost.server模块
RUN mvn clean package -pl '!boost.serverless' -DskipTests

# 最终镜像阶段
FROM openjdk:8-jdk

# 设置工作目录
WORKDIR /app

# 从后端构建阶段复制构建好的jar包到容器中
COPY --from=backend-builder /app/boost.server/target/boost.server-1.0.0-SNAPSHOT.jar .

# 暴露服务端口
EXPOSE 9999

# 指定容器启动执行命令
CMD ["java", "-jar", "boost.server-1.0.0-SNAPSHOT.jar"]
