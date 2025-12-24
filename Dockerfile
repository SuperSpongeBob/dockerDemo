# 多阶段构建：先编译，再打包运行镜像

# ========== 构建阶段 ==========
FROM eclipse-temurin:17 AS builder
#FROM eclipse-temurin:8 AS builder
#FROM openjdk:8-jre-slim AS builder
#FROM registry.cn-hangzhou.aliyuncs.com/ibbd/java:8-jre AS builder

WORKDIR /build

# 复制 Maven 配置和源码
COPY pom.xml .
COPY docker-demo-core/pom.xml docker-demo-core/
COPY docker-demo-chat/pom.xml docker-demo-chat/
COPY docker-demo-app/pom.xml docker-demo-app/

COPY .mvn .mvn
COPY mvnw .

# 下载依赖（利用 Docker 缓存）
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# 复制源码并构建
COPY docker-demo-core/src docker-demo-core/src
COPY docker-demo-chat/src docker-demo-chat/src
COPY docker-demo-app/src docker-demo-app/src

RUN ./mvnw clean package -DskipTests -B

# ========== 运行阶段 ==========
#FROM eclipse-temurin:17-jre
FROM eclipse-temurin:17
#FROM eclipse-temurin:8-jre
#FROM openjdk:8-jre-slim

LABEL maintainer="zyx <3024799675@qq.com>"

WORKDIR /app

# 从构建阶段复制 JAR（多模块项目 JAR 在 docker-demo-app/target 下）
COPY --from=builder /build/docker-demo-app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
