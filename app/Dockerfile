# Utiliser une image de base Java 22
FROM openjdk:22

# Copier le fichier JAR dans le conteneur
COPY build/libs/app-0.0.1.jar /app/app.jar

# Définir le répertoire de travail
WORKDIR /app

# Exposer le port 8080
EXPOSE 8080

# Exécuter l'application
CMD ["java", "-jar", "app.jar"]
#Dockerfile backend à verifier
