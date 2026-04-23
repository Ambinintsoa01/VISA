# Frontend Sprint 1 - Formulaire Titre de Séjour

## 📋 Description

Frontend multi-étapes pour la création de dossiers de titre de séjour à Madagascar.

## 🎯 Fonctionnalités

### Étape 1: État Civil
- Collecte des informations personnelles du demandeur
- Champs: Nom, Prénom, Sexe, Date de naissance, Nationalité, Situation familiale, Lieu de naissance, Adresse, Téléphone
- Validation des champs requis
- Chargement automatique des listes déroulantes

### Étape 2: Passeport & Visa Transformable
- Enregistrement des informations du passeport
- Saisie des données de visa transformable
- Type de visa: Investisseur, Travailleur, Regroupement Familial
- Dates de validité

### Étape 3: Pièces Justificatives
- Affichage de la checklist des pièces requises
- Statut de chaque pièce (Fourni / Non fourni)
- Upload des fichiers pour chaque pièce
- Barre de progression de complétude
- Validation avant création du dossier

## 🚀 Démarrage

### Prérequis
- Spring Boot 3.2.2 ou supérieur
- Java 17
- Maven

### Installation

1. **Compiler et lancer l'application**
```bash
mvn spring-boot:run
```

2. **Accéder au frontend**
```
http://localhost:8080/
```

## 📁 Structure des fichiers

```
src/main/resources/
├── templates/
│   ├── index.html                 # Page d'accueil
│   └── formulaire-dossier.html    # Formulaire multi-étapes
└── static/
    ├── css/
    │   └── formulaire-dossier.css # Styles du formulaire
    └── js/
        └── formulaire-dossier.js  # Logique du formulaire

src/main/java/mg/visa/controller/
├── HomeController.java             # Routes des pages
└── ... (contrôleurs API existants)
```

## 🔌 Endpoints API utilisés

### Référence (GET)
- `GET /api/ref/nationalites` - Liste des nationalités
- `GET /api/ref/situations-familiales` - Liste des situations familiales
- `GET /api/ref/types-identite` - Liste des types d'identité

### Création de dossier
- `POST /api/demandeurs` - Créer un demandeur (État civil)
- `POST /api/passeports` - Enregistrer un passeport
- `POST /api/visas` - Enregistrer un visa transformable
- `POST /api/demandes` - Créer une demande
- `POST /api/dossiers` - Créer le dossier avec pièces

### Gestion des pièces
- `GET /api/dossiers/{id}/pieces` - Liste des pièces du dossier
- `POST /api/dossiers/{id}/pieces/communes/{pieceId}/upload` - Upload une pièce commune
- `POST /api/dossiers/{id}/pieces/complementaires/{pieceId}/upload` - Upload une pièce complémentaire
- `GET /api/dossiers/{id}/completude` - Vérifier la complétude
- `PUT /api/dossiers/{id}/statut` - Changer le statut du dossier

## 📊 Flux de données

```
1. Chargement des données de référence
   ↓
2. Remplir Étape 1 (État civil)
   → POST /api/demandeurs
   ↓
3. Remplir Étape 2 (Passeport & Visa)
   → POST /api/passeports
   → POST /api/visas
   ↓
4. Créer le dossier (Étape 3)
   → POST /api/demandes
   → POST /api/dossiers
   → GET /api/dossiers/{id}/pieces
   ↓
5. Upload des pièces
   → POST /api/dossiers/{id}/pieces/communes/{pieceId}/upload
   ↓
6. Valider le dossier
   → PUT /api/dossiers/{id}/statut
```

## 🎨 Design

- **Framework CSS**: Bootstrap 5
- **Thème**: Gradient mauve/violet avec accents bleus
- **Animations**: Transitions fluides entre étapes
- **Responsive**: Optimisé pour desktop et mobile

## ⚙️ Configuration

### CORS
La configuration CORS est définie dans `CorsConfig.java` pour autoriser:
- Origins: `http://localhost:8080`, `http://localhost:3000`
- Méthodes: GET, POST, PUT, DELETE, OPTIONS
- Headers: All

## 🔄 Gestion des états

Le formulaire maintient un état global `formState` contenant:
- `currentStep`: Étape actuelle (1-3)
- `demandeurId`: ID du demandeur créé
- `passeportId`: ID du passeport créé
- `visaTransformableId`: ID du visa créé
- `dossierId`: ID du dossier créé
- `formData`: Données du formulaire sauvegardées

## ❌ Gestion des erreurs

- Validation des champs requis avant passage à l'étape suivante
- Messages d'alerte pour chaque erreur API
- Affichage des champs invalides avec border rouge
- Auto-masquage des messages d'alerte après 5 secondes

## 📱 Étapes du formulaire

### Étape 1: Validation
- Tous les champs d'état civil sont requis
- La sauvegarde crée automatiquement le demandeur

### Étape 2: Validation
- Tous les champs sont requis
- Sauvegarde du passeport et du visa

### Étape 3: Validation
- Affichage automatique de la checklist
- Création du dossier et de la demande
- Upload des pièces requises
- Vérification de la complétude

## 🐛 Dépannage

### Erreur "Erreur lors du chargement des listes déroulantes"
- Vérifier que les endpoints `/api/ref/**` répondent
- Vérifier la configuration CORS

### Upload de fichier échoue
- Vérifier que le chemin `file.upload-dir` est accessible
- Vérifier que la limite de taille de fichier est suffisante

### Pas de pièces affichées à l'étape 3
- Vérifier que le dossier a été créé correctement
- Vérifier que l'endpoint `/api/dossiers/{id}/pieces` retourne les données

## 📝 Notes

- Le formulaire utilise Thymeleaf comme moteur de template
- Les appels API sont faits en JavaScript vanilla (fetch API)
- Les données sont validées côté client et côté serveur
- Les IDs sont générés par le serveur lors de la création

## 🔐 Sécurité

- CSRF: À implémenter si nécessaire
- Validation serveur: Obligatoire pour tous les endpoints
- Upload de fichier: À sécuriser avec vérification de type MIME et taille

## 📞 Support

Pour toute question ou problème, consultez le fichier TODO.md pour les détails du sprint 1.
