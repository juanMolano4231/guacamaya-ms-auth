# ---- build stage ----
FROM gradle:8.14-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle build -x test

# ---- runtime stage ----
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java","-jar","app.jar"]