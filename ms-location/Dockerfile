# ---------- Étape 1 : construction du jar ----------
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Maven Wrapper et pom.xml d'abord : les dépendances sont mises en cache
# tant que le pom.xml ne change pas
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Sources (code Java + user.proto), puis build sans les tests
# (le test contextLoads a besoin de PostgreSQL, absent au moment du build)
COPY src src
RUN ./mvnw package -DskipTests -B

# ---------- Étape 2 : image d'exécution ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# Port REST de MS-Location (documentaire : il n'est pas publié sur l'hôte en Compose)
EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]