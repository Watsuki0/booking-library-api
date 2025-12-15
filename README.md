# Library API

API REST Spring Boot pour la gestion d'une bibliothèque avec système d'emprunts.

## Prérequis techniques

- Java 17 ou supérieur
- Maven 3.9+
- Base de données H2 (en mémoire, pas de Docker requis)

## Installation & Lancement

```bash
# Compiler le projet
mvn clean compile

# Lancer l'application
mvn spring-boot:run
```

L'API sera accessible sur `http://localhost:8080`.

### Accès à la base de données H2

- URL : `http://localhost:8080/h2-console`
- JDBC URL : `jdbc:h2:mem:librarydb`
- Username : `sa`
- Password : (vide)

## Architecture des données

### Diagramme de classes

```mermaid
classDiagram
    class Category {
        +Long id
        +String name
        +String description
        +List<Book> books
    }

    class Author {
        +Long id
        +String firstName
        +String lastName
        +String nationality
        +List<Book> books
    }

    class Book {
        +Long id
        +String isbn
        +String title
        +Integer publicationYear
        +Integer totalCopies
        +Integer availableCopies
        +Category category
        +List<Author> authors
        +List<Loan> loans
    }

    class User {
        +Long id
        +String username
        +String password
        +String email
        +Role role
        +List<Loan> loans
    }

    class Loan {
        +Long id
        +LocalDate loanDate
        +LocalDate dueDate
        +LocalDate returnDate
        +LoanStatus status
        +User user
        +Book book
    }

    class LoanStatus {
        <<enumeration>>
        ACTIVE
        RETURNED
        OVERDUE
    }

    class Role {
        <<enumeration>>
        USER
        ADMIN
    }

    Category ||--o{ Book : "1..*"
    Book ||--o{ Loan : "1..*"
    User ||--o{ Loan : "1..*"
    Book }o--o{ Author : "*..*"
```

### Relations entre entités

- **Category ↔ Book** : OneToMany / ManyToOne
- **Book ↔ Author** : ManyToMany
- **User ↔ Loan** : OneToMany / ManyToOne
- **Book ↔ Loan** : OneToMany / ManyToOne

## Fonctionnalités principales

- **CRUD complet** sur toutes les entités (Catégorie, Auteur, Livre, Utilisateur, Emprunt)
- **Système d'authentification** avec rôles USER et ADMIN via Spring Security
- **Gestion des emprunts** avec règles métier :
  - Limite de 3 emprunts actifs par utilisateur
  - Vérification de disponibilité des livres
  - Interdiction du double emprunt
  - Calcul automatique des dates d'échéance (14 jours)
  - Calcul des pénalités (0.50€ par jour de retard)
- **Recherches avancées** (par ISBN, titre, nom d'auteur)
- **Validation des données** avec Bean Validation
- **Base de données H2** en mémoire avec données de démonstration