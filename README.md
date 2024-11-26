# Backend
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE.txt)
[![Release](https://github.com/Devops-noah/Backend/releases/tag/v1.0.0)]

[![Build & test](https://github.com/Devops-noah/Backend/actions/workflows/gradle-build.yml/badge.svg?branch=main)](https://github.com/Devops-noah/template-java-API/actions/workflows/gradle-build.yml)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Devops-noah_Backend&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Devops-noah_Backend)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=Devops-noah_Backend&metric=bugs)](https://sonarcloud.io/summary/new_code?id=Devops-noah_Backend)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=Devops-noah_Backend&metric=coverage)](https://sonarcloud.io/summary/new_code?id=Devops-noah_Backend)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=Devops-noah_Backend&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=Devops-noah_Backend)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=Devops-noah_Backend&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=Devops-noah_Backend)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=Devops-noah_Backend&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=Devops-noah_Backend)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=Devops-noah_Backend&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=Devops-noah_Backend)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=Devops-noah_Backend&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=Devops-noah_Backend)

# Backend Api Structure

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

