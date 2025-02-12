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
│
├── .github/               # Dossier pour les fichiers de configuration GitHub (actions, workflows, etc.)
├── .gradle/               # Dossier de configuration de Gradle
├── .settings/              # Dossier pour les paramètres d'IDE (par exemple, Eclipse)
├── app/                    # Dossier contenant le code principal de l'application
│   ├── bin/                # Dossier pour les fichiers binaires générés
│   ├── build/              # Dossier pour les fichiers de construction (build)
│   ├── gradle/             # Dossier de configuration de Gradle spécifique à l'application
│   └── src/                # Dossier source contenant tout le code Java
│       ├── docs/           # Dossier pour la documentation
│       └── main/           # Code source principal de l'application
│           ├── java/      # Dossier contenant tout le code Java
│           │   └── fr/    # Dossier pour les packages Java organisés par domaine
│           │       └── parisnanterre/ # Dossier pour ton projet spécifique
│           │           └── noah/   # Dossier pour ton package personnel
│           │               ├── config/     # Dossier pour la configuration de l'application
│           │               │   └── SecurityConfig.java  # Fichier de configuration de sécurité
│           │               ├── Controller/ # Dossier pour les contrôleurs (API)
│           │               │   └── admin/ # Dossier pour les contrôleurs d'administration
│           │               │       ├── AdminAnnounceController.java  # Gestion des annonces admin
│           │               │       ├── AdminController.java           # Contrôleur principal admin
│           │               │       ├── AdminNotationCommentsController.java  # Gestion des commentaires de notation
│           │               │       ├── AnnonceController.java         # Contrôleur pour la gestion des annonces
│           │               │       ├── AuthController.java           # Contrôleur pour l'authentification
│           │               │       ├── DemandeController.java        # Contrôleur pour les demandes
│           │               │       ├── DemandeTransferController.java # Contrôleur pour les transferts de demandes
│           │               │       ├── HelloController.java          # Contrôleur de test (Hello)
│           │               │       ├── InformationColisController.java # Contrôleur pour les informations de colis
│           │               │       ├── LivraisonController.java      # Contrôleur pour la gestion des livraisons
│           │               │       ├── NotationController.java       # Contrôleur pour les notations
│           │               │       ├── NotificationController.java   # Contrôleur pour les notifications
│           │               │       ├── PaysController.java           # Contrôleur pour les pays
│           │               │       ├── UtilisateurController.java    # Contrôleur pour les utilisateurs
│           │               │       └── VoyageController.java         # Contrôleur pour les voyages
│           │               ├── DTO/        # Dossier pour les Data Transfer Objects
│           │               │   ├── AnnonceRequest.java        # DTO pour les requêtes d'annonces
│           │               │   ├── AnnonceResponse.java       # DTO pour les réponses d'annonces
│           │               │   ├── AuthenticationRequest.java  # DTO pour les requêtes d'authentification
│           │               │   ├── AuthenticationResponse.java # DTO pour les réponses d'authentification
│           │               │   ├── DemandeRequest.java        # DTO pour les requêtes de demande
│           │               │   ├── DemandeResponse.java       # DTO pour les réponses de demande
│           │               │   ├── Filtre.java                # DTO pour les filtres de recherche
│           │               │   ├── InformationColisRequest.java  # DTO pour les requêtes d'information colis
│           │               │   ├── InformationColisResponse.java # DTO pour les réponses d'information colis
│           │               │   ├── NotationRequest.java       # DTO pour les requêtes de notation
│           │               │   ├── NotationResponse.java      # DTO pour les réponses de notation
│           │               │   ├── NotificationResponseDto.java  # DTO pour les réponses de notification
│           │               │   ├── UtilisateurProfileResponse.java # DTO pour la réponse du profil utilisateur
│           │               │   ├── UtilisateurRequest.java    # DTO pour les requêtes utilisateur
│           │               │   ├── VoyageRequest.java         # DTO pour les requêtes de voyage
│           │               │   └── VoyageResponse.java        # DTO pour les réponses de voyage
│           │               ├── Entity/     # Dossier pour les entités de la base de données
│           │               │   ├── Annonce.java    # Entité pour une annonce
│           │               │   ├── Demande.java    # Entité pour une demande
│           │               │   ├── Expedition.java # Entité pour un expéditeur
│           │               │   ├── Feedback.java  # Entité pour un retour d'utilisateur
│           │               │   ├── InformationColis.java # Entité pour l'information des colis
│           │               │   ├── Livraison.java  # Entité pour les livraisons
│           │               │   ├── Notation.java  # Entité pour les notations
│           │               │   ├── Notification.java # Entité pour les notifications
│           │               │   ├── Pays.java      # Entité pour les pays
│           │               │   ├── Role.java      # Entité pour les rôles des utilisateurs
│           │               │   ├── Segment.java   # Entité pour les segments de transport
│           │               │   ├── Statut.java    # Entité pour les statuts
│           │               │   ├── Suivi.java     # Entité pour les suivis
│           │               │   ├── TypeNotification.java # Entité pour les types de notification
│           │               │   ├── Utilisateur.java # Entité pour un utilisateur
│           │               │   ├── Voyage.java    # Entité pour un voyage
│           │               │   └── Voyageur.java  # Entité pour un voyageur
│           │               ├── Repository/ # Dossier pour les classes d'accès aux données
│           │               │   ├── AnnonceRepository.java  # Repository pour les annonces
│           │               │   ├── DemandeRepository.java  # Repository pour les demandes
│           │               │   ├── InformationColisRepository.java  # Repository pour l'information des colis
│           │               │   ├── LivraisonRepository.java  # Repository pour les livraisons
│           │               │   ├── NotationRepository.java  # Repository pour les notations
│           │               │   ├── NotificationRepository.java  # Repository pour les notifications
│           │               │   ├── PaysRepository.java  # Repository pour les pays
│           │               │   ├── SegmentRepository.java  # Repository pour les segments
│           │               │   ├── SuiviRepository.java  # Repository pour les suivis
│           │               │   ├── UtilisateurRepository.java  # Repository pour les utilisateurs
│           │               │   └── VoyageRepository.java  # Repository pour les voyages
│           │               ├── Service/    # Dossier pour la logique métier
│           │               │   ├── admin/  # Dossier pour les services d'administration
│           │               │   │   ├── AdminServiceImpl.java   # Implémentation des services d'admin
│           │               │   │   ├── AnnonceServiceImpl.java  # Service pour les annonces
│           │               │   │   ├── DemandeService.java      # Service pour les demandes
│           │               │   │   ├── DemandeTransferService.java # Service pour les transferts de demandes
│           │               │   │   ├── ImageServiceImpl.java    # Service pour la gestion des images
│           │               │   │   ├── InformationColisService.java # Service pour les informations de colis
│           │               │   │   ├── LivraisonService.java    # Service pour les livraisons
│           │               │   │   ├── NotationService.java     # Service pour les notations
│           │               │   │   ├── NotificationService.java  # Service pour les notifications
│           │               │   │   ├── PaysServiceImpl.java    # Service pour les pays
│           │               │   │   ├── UtilisateurServiceImpl.java  # Service pour les utilisateurs
│           │               │   │   └── VoyageServiceImpl.java   # Service pour les voyages
│           │               │   └── autres services (par exemple, des services non administratifs)
│           │               └── util/       # Dossier pour les utilitaires généraux
│           │                   ├── JwtAuthenticationFilter.java  # Filtre pour l'authentification JWT
│           │                   ├── JwtUtil.java                   # Utilitaires pour la gestion de JWT
│           │                   └── TravelCarryApplication.java   # Classe principale de l'application
│           └── resources/   # Dossier pour les fichiers de ressources comme la configuration (fichiers YAML, properties, etc.)
│               └── scripts/  # Dossier pour les scripts (par exemple, pour l'automatisation)
│                   ├── gitfame.rb        # Script Ruby pour l'outil Gitfame
│                   └── application.yml   # Fichier de configuration pour l'application
├── test/                                                     # Unit and integration tests
│   └── java/
│       └── com/
│           └── yourcompany/
│               └── travelcarry/
│                   ├── controller/                      # Tests for controllers
│                   ├── service/                         # Tests for services
└── build.gradle                              # Configuration de Gradle pour les tests


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
    Creation de voyage et d'annonce avec validation et soumission.
    Gestion des colis ( proposition de colis ):  création, soumission et validation.
    Notifications des proposition de colis et des feedbacks de reponse d'acceptation ou de rejet pour les expéditeurs.
    Gestion des annonces et voyages pour les voyageurs.
    recherche de chaine de transfere pour des segments d'annonce qui existe et qui constituent ensemble un chemin complet.
    Notation des utilisateur qui sera accepter ou rejeter par l'admin avant qu'il soit visible par tous le monde.

## 🛠️ Outils de Développement

    IDE : IntelliJ IDEA / Eclipse / VS Code.
    Base de Données : PostgreSQL.
    Neon pour la base de donnée en ligne : https://console.neon.tech/app/projects/misty-paper-89322152/branches/br-little-shape-a2dhwb4o/tables?database=travel_carry_db
    Postman : Test des API.


+ Add "org.springdoc:springdoc-openapi-ui" dependency
+ Fill AnnonceController class with tags (It is for all Controller)
+ Go to [API SWAGGER] (http://localhost:8080/swagger-ui.html)

## lien youtub https://youtu.be/OPco7TGWPa8
