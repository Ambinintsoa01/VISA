# Guide de démarrage rapide - Frontend Sprint 1

## ✅ Fichiers créés

### Templates Thymeleaf
1. **`src/main/resources/templates/index.html`**
   - Page d'accueil avec lien vers le formulaire

2. **`src/main/resources/templates/formulaire-dossier.html`**
   - Formulaire multi-étapes principal
   - 3 étapes progressives
   - Barre de progression
   - Messages d'alerte

### Fichiers Statiques
3. **`src/main/resources/static/css/formulaire-dossier.css`**
   - Styles complets du formulaire
   - Design responsive
   - Animations et transitions

4. **`src/main/resources/static/js/formulaire-dossier.js`**
   - Logique du formulaire
   - Gestion des étapes
   - Appels API
   - Validation des formulaires

### Contrôleurs Java
5. **`src/main/java/mg/visa/controller/HomeController.java`**
   - Route GET `/` → index.html
   - Route GET `/formulaire-dossier` → formulaire-dossier.html

### Configuration
6. **`src/main/java/mg/visa/config/CorsConfig.java`**
   - Configuration CORS
   - Autorisation des appels API cross-origin

## 🚀 Pour lancer le projet

```bash
# 1. À la racine du projet (f:\VISA)
cd f:\VISA

# 2. Lancer Maven Spring Boot
mvn spring-boot:run

# 3. Ouvrir le navigateur
http://localhost:8080/

# 4. Cliquer sur "Nouveau Dossier"
# ↓
# http://localhost:8080/formulaire-dossier
```

## 📊 Structure des 3 étapes

### Étape 1: État Civil ✍️
```
Champs:
- Nom (requis)
- Prénom (requis)
- Sexe (requis)
- Date de Naissance (requis)
- Nationalité (requis, liste déroulante)
- Situation Familiale (requis, liste déroulante)
- Lieu de Naissance (requis)
- Adresse (requis)
- Téléphone de Contact (requis)

Action: POST /api/demandeurs
```

### Étape 2: Passeport & Visa 📖
```
Passeport:
- Numéro (requis)
- Date d'Émission (requis)
- Date d'Expiration (requis)
- Lieu d'Émission (requis)

Visa Transformable:
- Type de Visa (requis)
- Date de Début (requis)
- Date de Fin (requis)
- Type d'Identité (requis, liste déroulante)

Actions: 
- POST /api/passeports
- POST /api/visas
```

### Étape 3: Pièces Justificatives 📋
```
Affichage automatique:
- Liste de toutes les pièces requises
- Statut de chaque pièce (Fourni/Non fourni)
- Barre de progression

Actions:
- Upload des fichiers manquants
- Validation du dossier complet
- Création du dossier avec statut APPROUVE
```

## 🔑 Points importants

1. **Validation progressive**
   - Chaque étape doit être complète avant de passer à la suivante
   - Les champs manquants sont surlignés en rouge

2. **Sauvegarde automatique**
   - Les données sont sauvegardées au changement d'étape
   - Pas besoin de formulaire de confirmation

3. **Chargement des listes**
   - Nationalités, situations familiales et types d'identité sont chargés au démarrage
   - Les listes déroulantes se remplissent automatiquement

4. **Gestion des IDs**
   - Chaque ressource créée génère un ID utilisé pour les étapes suivantes
   - L'ID du dossier final est affiché dans une modal de succès

## 🔗 Endpoints API requuis (backend)

### Données de référence (GET)
```
GET /api/ref/nationalites
GET /api/ref/situations-familiales
GET /api/ref/types-identite
```

Retour attendu: Array de {id, name/libelle/label}

### Création de ressources (POST)
```
POST /api/demandeurs
POST /api/passeports
POST /api/visas
POST /api/demandes
POST /api/dossiers
```

### Gestion des pièces
```
GET /api/dossiers/{id}/pieces
POST /api/dossiers/{id}/pieces/communes/{pieceId}/upload
POST /api/dossiers/{id}/pieces/complementaires/{pieceId}/upload
GET /api/dossiers/{id}/completude
PUT /api/dossiers/{id}/statut
```

## 💡 Astuces

1. **Tester avec le navigateur**
   - F12 → Console pour voir les logs JavaScript
   - F12 → Network pour voir les appels API

2. **Messages d'alerte**
   - Succès (vert) → L'action s'est bien déroulée
   - Erreur (rouge) → Problème API ou validation
   - Avertissement (jaune) → Information importante

3. **Boutons de navigation**
   - "Précédent" → Retour à l'étape précédente
   - "Suivant" → Validation et passage à l'étape suivante
   - "Créer le Dossier" → Finalisation et création

4. **Réinitialiser le formulaire**
   - Bouton "Réinitialiser" → Efface tous les champs

## 🐛 Problèmes courants et solutions

### Le formulaire ne s'affiche pas
- Vérifier que Spring Boot s'est lancé correctement
- Vérifier l'absence d'erreurs dans la console

### Les listes déroulantes sont vides
- Vérifier que les endpoints `/api/ref/**` répondent
- Vérifier la structure JSON retournée

### L'upload de fichier échoue
- Vérifier que le dossier d'upload existe
- Vérifier les permissions en écriture

### "Erreur lors de l'enregistrement du demandeur"
- Vérifier que l'endpoint POST /api/demandeurs existe
- Vérifier que les champs obligatoires sont présents

## 📚 Documentation supplémentaire

- Voir `FRONTEND-SPRINT1-README.md` pour les détails complets
- Voir `TODO.md` pour les spécifications du sprint 1
- Voir la structure du projet pour comprendre l'architecture

## ✨ Prochaines étapes

- Sprint 2: Formulaire de duplicata
- Sprint 3: Upload de pièces et validation finale
- Ajouter l'authentification
- Implémenter la gestion des erreurs côté backend
- Ajouter les tests unitaires et d'intégration
