# Étape 1: Build de l'application
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copier le fichier pom.xml et télécharger les dépendances
COPY pom.xml .
RUN mvn dependency:go-offline

# Copier le code source et compiler l'application
COPY src ./src
RUN mvn clean package -DskipTests

# Étape 2: Exécution de l'application
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copier le fichier jar généré depuis l'étape de build
COPY --from=build /app/target/*.jar app.jar

# Exposer le port (sera utilisé par la variable d'environnement PORT sur Render)
EXPOSE 8080

# Démarrer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]