# Backend
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE.txt)
![Latest release](https://img.shields.io/github/v/release/Devops-noah/Backend)

[![Java CI Gradle build and test](https://github.com/Devops-noah/Backend/actions/workflows/gradle-build-test.yml/badge.svg)](https://github.com/Devops-noah/Backend/actions/workflows/gradle-build-test.yml)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Devops-noah_Backend&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Devops-noah_Backend)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=Devops-noah_Backend&metric=bugs)](https://sonarcloud.io/summary/new_code?id=Devops-noah_Backend)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=Devops-noah_Backend&metric=coverage)](https://sonarcloud.io/summary/new_code?id=Devops-noah_Backend)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=Devops-noah_Backend&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=Devops-noah_Backend)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=Devops-noah_Backend&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=Devops-noah_Backend)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=Devops-noah_Backend&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=Devops-noah_Backend)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=Devops-noah_Backend&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=Devops-noah_Backend)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=Devops-noah_Backend&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=Devops-noah_Backend)

# Backend Api Structure

## 🚀 Travel Carry - Backend

Ce projet constitue la partie Backend de l'application Travel Carry, développée avec Spring Boot. Il gère les API pour la gestion des utilisateurs, des colis et des voyages.
```
Backend/
├── app
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── fr/
│   │   │       └── parisnanterre/
│   │   │           └── noah (travelcarry)/
│   │   │               ├── TravelCarryApplication.java           # Main application class
│   │   │               ├── config/                               # Configuration files
│   │   │               │   ├── DatabaseConfig.java               # Database-related configuration (if customizing beyond application.properties)
│   │   │               │   ├── MailConfig.java                   # Email service configuration (if sending emails)
│   │   │               ├── controller/                           # REST controllers
│   │   │               │   ├── UtilisateurController.java        # Handles user-related requests
│   │   │               │   ├── AnnonceController.java            # Handles annonce-related requests
│   │   │               │   ├── VoyageController.java             # Handles voyage-related requests
│   │   │               │   └── PaysController.java               # Handles pays-related requests
│   │   │               ├── model/                                # Entity classes matching the class diagram
│   │   │               │   ├── Utilisateur.java                  # User entity with properties and roles
│   │   │               │   ├── Annonce.java                      # Annonce entity
│   │   │               │   ├── Voyage.java                       # Voyage entity
│   │   │               │   └── Pays.java                         # Country entity
│   │   │               ├── DTO/  
│   │   │               │   ├── Filtre.java                                                     
│   │   │               ├── repository/                           # Data access layer (JPA repositories)
│   │   │               │   ├── UtilisateurRepository.java        # Repository for users
│   │   │               │   ├── AnnonceRepository.java            # Repository for annonces
│   │   │               │   ├── VoyageRepository.java             # Repository for voyages
│   │   │               │   └── PaysRepository.java               # Repository for country info
│   │   │               ├── service/                              # Business logic layer
│   │   │               │   ├── UtilisateurService.java           # Service for user-related operations
│   │   │               │   ├── AnnonceService.java               # Service for annonces
│   │   │               │   ├── VoyageService.java                # Service for voyages
│   │   │               ├── security/                             # Security configuration (if applicable)
│   │   │               │   ├── JwtAuthenticationFilter.java      # JWT filter to validate tokens on requests
│   │   │               │   ├── JwtTokenProvider.java             # Utility for generating and validating JWT tokens
│   │   │               │   ├── CustomUserDetailsService.java     # Service for loading user-specific data during authentication
│   │   │               │   ├── SecurityConstants.java            # Constants for security (e.g., secret key, token expiration time)
│   │   │               │   ├── SecurityConfig.java               # Main security configuration class for HTTP security
│   │   │               │   └── RolePermissions.java              # Defines permissions for roles (if using role-based access control)
│   │   └── resources/
│   │       ├── application.yml                            # Application configuration
│   │       └── static/                                           # Static files (if needed)
│   └── test/                                                     # Unit and integration tests
│       └── java/
│           └── com/
│               └── yourcompany/
│                   └── travelcarry/
│                       ├── controller/                      # Tests for controllers
│                       ├── service/                         # Tests for services
└── build.gradle                                             # build.gradle for Gradle dependencies

```

## Api Documentation

## 🛠️ Technologies Utilisées

    Spring Boot : Framework principal pour le backend.
    Spring Data JPA : Accès à la base de données.
    Spring Security : Gestion de l'authentification et de l'autorisation.
    JWT (JSON Web Tokens) : Sécurité pour l'authentification.
    PostgreSQL : Base de données relationnelle.
    Gradle : Outil de gestion des dépendances.
    Lombok : Réduction de la verbosité dans le code.
    Swagger : Documentation interactive des API.

## 🚀 Installation et Configuration

### 1. Prérequis

    Java 21 installé sur votre machine.
    Gradle installé.
    PostgreSQL pour la base de données.
### 2. Installation des Dépendances

Clonez le projet et installez les dépendances avec Gradle :

git clone https://github.com/Devops-noah/Backend.git
cd Backend
./gradlew build

### 3. Exécution de l'Application

Lancez l'application avec Gradle :

./gradlew bootRun

L'API sera disponible à l'adresse :
http://localhost:8080
## 🔐 Sécurité

    L'application utilise JWT pour sécuriser les endpoints.
    Lors de la connexion (/api/auth/login), un token JWT est généré. Ce token doit être inclus dans l'en-tête Authorization pour chaque requête protégée.

## 📄 Documentation des API

    Swagger UI est disponible pour tester et explorer les endpoints.
    Accédez à http://localhost:8080/swagger-ui.html une fois l'application démarrée.

## 🚀 Lancement en Production

Pour générer un fichier .jar exécutable, utilisez Gradle :

./gradlew build

Le fichier .jar sera disponible dans le dossier build/libs/ et pourra être exécuté avec :

java -jar build/libs/travelcarry.jar

## 🔧 Tests

Pour exécuter les tests unitaires et d'intégration, utilisez la commande suivante :

./gradlew test

## 📢 Fonctionnalités Principales

    Authentification sécurisée (Spring Security + JWT).
    Gestion des colis : création, soumission et validation.
    Notifications des feedbacks pour les expéditeurs.
    Gestion des annonces et voyages pour les voyageurs.

## 🛠️ Outils de Développement

    IDE : IntelliJ IDEA / Eclipse / VS Code.
    Base de Données : PostgreSQL.
    Postman : Test des API.


+ Add "org.springdoc:springdoc-openapi-ui" dependency
+ Fill AnnonceController class with tags (It is for all Controller)
+ Go to [API SWAGGER] (http://localhost:8080/swagger-ui.html)
