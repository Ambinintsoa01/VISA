# BACK-OFFICE TITRE DE SÉJOUR MADAGASCAR

## Structure Spring Boot MVC  +  Todo liste des 3 sprints

## STRUCTURE DU PROJET

```
titre-sejour-backend/
│
├── src/main/java/mg/visa
│   │
│   │
│   ├── entity/                         # Une classe par table
│   │   ├── ref/
│   │   │   ├── StatutDossier.java
│   │   │   ├── StatutPiece.java
│   │   │   ├── Nationalite.java
│   │   │   ├── SituationFamiliale.java
│   │   │   └── TypeIdentite.java
│   │   │
│   │   ├── Demandeur.java
│   │   ├── Passeport.java
│   │   ├── VisaTransformable.java
│   │   ├── Dossier.java
│   │   ├── CataloguePieceCommune.java
│   │   ├── CataloguePieceComplementaire.java
│   │   ├── DossierPieceCommune.java
│   │   └── DossierPieceComplementaire.java
│   │
│   ├── repository/                     # JpaRepository — une par entity
│   │   ├── ref/
│   │   │   ├── StatutDossierRepository.java
│   │   │   ├── StatutPieceRepository.java
│   │   │   ├── NationaliteRepository.java
│   │   │   ├── SituationFamilialeRepository.java
│   │   │   └── TypeIdentiteRepository.java
│   │   │
│   │   ├── DemandeurRepository.java
│   │   ├── PasseportRepository.java
│   │   ├── VisaTransformableRepository.java
│   │   ├── DossierRepository.java
│   │   ├── CataloguePieceCommuneRepository.java
│   │   ├── CataloguePieceComplementaireRepository.java
│   │   ├── DossierPieceCommuneRepository.java
│   │   └── DossierPieceComplementaireRepository.java
│   │
│   ├── dto/                            # Objets de transfert (pas d'entity dans le front)
│   │   ├── DemandeurDTO.java           # état civil complet
│   │   ├── PasseportDTO.java
│   │   ├── VisaTransformableDTO.java
│   │   ├── DossierCreationDTO.java     # regroupe les 3 étapes du formulaire
│   │   ├── PieceCommuneDTO.java        # id + statut + fichier
│   │   └── PieceComplementaireDTO.java
│   │
│   ├── service/
│   │   ├── RefDataService.java         # Charge les listes déroulantes (nationalite, etc.)
│   │   ├── DemandeurService.java
│   │   ├── DossierService.java         # Logique métier centrale
│   │   ├── PieceService.java           # Upload + mise à jour statut
│   │   └── FileStorageService.java     # Enregistre physiquement les fichiers
│   │
│   └── controller/
│       ├── RefDataController.java      # GET /api/ref/** (listes)
│       ├── DemandeurController.java    # POST /api/demandeurs
│       ├── DossierController.java      # CRUD dossier + changement statut
│       └── PieceController.java        # Upload pièces
│
├── src/main/resources/
│   ├── application.properties          # DB, upload path, port
│   └── db/
│       └── schema_v2.sql               # Script SQL complet
│
└── pom.xml
```

## DÉPENDANCES pom.xml (essentielles)

```xml
<!-- Spring Web (REST Controllers) -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- Spring Data JPA -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- Driver PostgreSQL -->
<dependency>
  <groupId>org.postgresql</groupId>
  <artifactId>postgresql</artifactId>
</dependency>

<!-- Lombok (évite les getters/setters manuels) -->
<dependency>
  <groupId>org.projectlombok</groupId>
  <artifactId>lombok</artifactId>
  <optional>true</optional>
</dependency>

<!-- Validation (@NotBlank, @NotNull sur les DTO) -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

## application.properties

```properties
# Base de données
spring.datasource.url=jdbc:postgresql://localhost:5432/titre_sejour_db
spring.datasource.username=postgres
spring.datasource.password=yourpassword

# JPA — ne recrée pas le schéma (on utilise schema_v2.sql)
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true

# Upload fichiers
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=30MB
file.upload-dir=/var/titresejour/uploads
```

## ENDPOINTS REST PAR SPRINT

```
SPRINT 1 — Formulaire nouveau titre
  GET  /api/ref/nationalites
  GET  /api/ref/situations-familiales
  GET  /api/ref/types-identite
  POST /api/demandeurs                      ← étape 1 : état civil
  POST /api/passeports                      ← étape 2 : passeport
  POST /api/visas                           ← étape 2 : visa transformable
  POST /api/dossiers                        ← étape 3 : crée le dossier complet
  GET  /api/dossiers/{id}/pieces            ← checklist pièces du dossier

SPRINT 2 — Récupération dossier existant
  GET  /api/dossiers/{id}                   ← charger un dossier
  GET  /api/demandeurs/{id}                 ← charger un demandeur existant
  PUT  /api/dossiers/{id}                   ← modifier un dossier

SPRINT 3 — Validation et upload
  POST /api/dossiers/{id}/pieces/communes/{pieceId}/upload
  POST /api/dossiers/{id}/pieces/complementaires/{pieceId}/upload
  GET  /api/dossiers/{id}/completude        ← vérifie si toutes les pièces sont fournies
  PUT  /api/dossiers/{id}/statut            ← passe à APPROUVE ou REJETE
```

## TODO LISTE — 3 SPRINTS

### SPRINT 1 — Formulaire nouveau titre (création complète)

**Objectif :** Un agent remplit le formulaire étape par étape et crée le dossier.

#### Backend

- [ ] Créer le projet Spring Boot (Spring Initializr)
- [ ] Configurer `application.properties` (DB + upload)
- [ ] Exécuter `schema_v2.sql` sur PostgreSQL
- [ ] Créer toutes les `@Entity` (entity/)
- [ ] Créer tous les `JpaRepository` (repository/)
- [ ] `RefDataController` — exposer les listes déroulantes
  - GET nationalités, situations familiales, types d'identité
- [ ] `DemandeurService` + `DemandeurController`
  - POST /api/demandeurs → enregistre l'état civil
- [ ] `PasseportService` + endpoints passeport
  - POST /api/passeports → lié au demandeur
- [ ] `VisaTransformableService` + endpoints visa
  - POST /api/visas → lié au passeport
- [ ] `DossierService` — méthode `creerDossier()`
  - POST /api/dossiers → crée dossier + initialise toutes les pièces à NON_FOURNI
- [ ] `DossierRepository` — méthode `findPiecesParDossier(id)`

#### Frontend (si applicable)

- [ ] Étape 1 : formulaire état civil (nom, prénom, nationalité, situation familiale…)
- [ ] Étape 2 : formulaire passeport + visa transformable
- [ ] Étape 3 : sélection type d'identité + affichage checklist pièces
- [ ] Bouton "Suivant" entre chaque étape (validation des champs requis)
- [ ] Confirmation de création du dossier


**A respecter :** 
- Demandeur correspond a une table dans la bdd
- Visa transformable correspond a une table dans la bdd
- Passport transformable correspond a une table dans la bdd
- Demande transformable correspond a une table dans la bdd
- Type demande transformable correspond a une table dans la bdd
    - nouveau titre
    - transfert visa
    - ...
- Type Visa transformable correspond a une table dans la bdd
    - investisseur
    - travailleur
- Status demande correspond a une table dans la bdd
    - Crée
    - Appprouvée
    - Rejetée
- Historique status demande correspond a une table dans la bdd
- Situation familliale / sexe / nationnalite correspond a une table dans la bdd (type objet)
- Visa correspond a une table dans la bdd (id_demande)
- Carte resident demande correspond a une table dans la bdd

---

### SPRINT 2 — Duplicata (visa ou carte de résident perdu, sans historique système)

**Contexte :**
Le demandeur possédait un visa ou une carte de résident délivré dans l'ancien
système papier. Il l'a perdu ou volé. Le nouveau système n'a aucune trace de
ce document. Pour reconstruire son dossier, il apporte physiquement les
photocopies de ses anciens documents (ex: ref, dates).
L'agent back-office saisit les informations à partir de ces photocopies.
Le résultat final est une décision manuelle de l'agent : recréer un nouveau
visa ou une nouvelle carte de résident avec les mêmes informations reconstituées.


#### Frontend (si applicable)

* [ ] Étape 1 : même formulaire que Sprint 1 (état civil + passeport + pièces communes
  et complémentaires selon type) + upload des photocopies justificatives
* [ ] Étape 2 : saisie du document à récupérer
  * Choix : Visa ou Carte de résident
  * Référence (numéro reconstruit depuis la photocopie)
  * Date début et date fin
* [ ] Bouton "Soumettre"

---

### SPRINT 3 — Upload pièces + validation finale

**Objectif :** L'agent uploade chaque pièce justificative et valide le dossier si complet.

#### Backend

- [ ] `FileStorageService` — enregistre le fichier sur disque, retourne le chemin
- [ ] `PieceService` — méthode `uploadPiece()`
  - Reçoit le fichier (MultipartFile)
  - Enregistre `fichier_path` + passe statut_piece à FOURNI
  - POST /api/dossiers/{id}/pieces/communes/{pieceId}/upload
  - POST /api/dossiers/{id}/pieces/complementaires/{pieceId}/upload
- [ ] `DossierService` — méthode `verifierCompletude(dossierId)`
  - Retourne true si toutes les pièces obligatoires sont à FOURNI
  - GET /api/dossiers/{id}/completude
- [ ] `DossierService` — méthode `changerStatut(dossierId, statut)`
  - PUT /api/dossiers/{id}/statut → APPROUVE ou REJETE
  - Bloque la validation si le dossier n'est pas complet

#### Frontend (si applicable)

- [ ] Affichage de la checklist pièces avec statut visuel (✓ / ✗)
- [ ] Bouton "Choisir fichier" pour chaque pièce
- [ ] Barre de progression (X / 10 pièces fournies)
- [ ] Bouton "Valider le dossier" (actif seulement si complet)
- [ ] Confirmation d'approbation ou formulaire de motif de rejet
