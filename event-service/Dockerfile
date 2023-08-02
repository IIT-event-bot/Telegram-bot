FROM maven as builder
COPY . /src
WORKDIR /src
RUN mvn clean package -DskipTests

FROM maven
WORKDIR /app
COPY --from=builder /src/target/*.jar app.jar
EXPOSE 8001
ENTRYPOINT ["java", "-jar", "app.jar"]